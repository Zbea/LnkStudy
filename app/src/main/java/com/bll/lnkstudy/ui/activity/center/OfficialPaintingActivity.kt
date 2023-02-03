package com.bll.lnkstudy.ui.activity.center

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.mvp.model.PaintingList
import com.bll.lnkstudy.mvp.model.PopWindowData
import com.bll.lnkstudy.mvp.presenter.CenterPaintingPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AppWallpaperListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_official_painting.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import kotlin.math.ceil

class OfficialPaintingActivity:BaseAppCompatActivity(),IContractView.IPaintingView{

    private val presenter=CenterPaintingPresenter(this)
    private var flags=0//1壁纸2书画
    private var items= mutableListOf<PaintingList.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var pageSize=12
    private var mAdapter:AppWallpaperListAdapter?=null

    private var popTimes= mutableListOf<PopWindowData>()
    private var popPaintings= mutableListOf<PopWindowData>()
    private var popWindowTime:PopWindowList?=null
    private var popWindowPainting:PopWindowList?=null
    private var position=0

    private var dynasty=0 //年代
    private var paintingType=0 //书画内容

    override fun onList(bean: PaintingList?) {
        pageCount = ceil(bean?.total?.toDouble()!! / pageSize).toInt()
        val totalCount = bean.total
        if (totalCount == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        items=bean?.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
        items[position].buyStatus=1
        mAdapter?.notifyDataSetChanged()
    }

    override fun layoutId(): Int {
        return R.layout.ac_official_painting
    }

    override fun initData() {
        flags=intent.flags

        val yeas= DataBeanManager.getIncetance().YEARS
        for (i in yeas.indices){
            popTimes.add(PopWindowData(i+1, yeas[i], i == 0))
        }
        dynasty=popTimes[0].id

        val paintings= DataBeanManager.getIncetance().PAINTING
        for (i in paintings.indices){
            popPaintings.add(PopWindowData(i+1, paintings[i], i == 0))
        }
        paintingType=popPaintings[0].id

        fetchData()
    }

    override fun initView() {
        setPageTitle(if (flags==1) "官方壁纸" else "官方书画")
        ll_painting.visibility=if (flags==1) View.GONE else View.VISIBLE

        initRecyclerView()

        tv_time.text=popTimes[0].name
        tv_time.setOnClickListener {
            selectorTime()
        }
        tv_painting_type.text=popPaintings[0].name
        tv_painting_type.setOnClickListener {
            selectorPainting()
        }

        btn_page_up.setOnClickListener {
            if (pageIndex>1){
                if(pageIndex<pageCount){
                    pageIndex-=1
                    fetchData()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }

    }

    private fun initRecyclerView(){

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = AppWallpaperListAdapter(R.layout.item_app_wallpaper, items)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),30))
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            ImageDialog(this,items[position].bodyUrl).builder()
        }
        mAdapter?.setOnItemChildClickListener{ adapter, view, position ->
            this.position=position
            val item=items[position]
            if (view.id==R.id.btn_download){
                if (item.buyStatus==1){
                    val paintingBean=PaintingBeanDaoManager.getInstance().queryBean(item.fontDrawId)
                    if (paintingBean==null){
                        onDownload()
                    }
                    else{
                        showToast("已下载")
                    }
                }
                else{
                    val map = HashMap<String, Any>()
                    map["type"] = 5
                    map["bookId"] = item.fontDrawId
                    presenter.buy(map)
                }
            }
        }
    }

    /**
     * 请求数据
     */
    private fun fetchData(){
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["supply"]=1
        map["type"]=flags
        if (flags==2){
            if (paintingType!=5&&paintingType!=6){
                map["dynasty"] =dynasty
            }
            map["subType"]=paintingType
        }
        presenter.getList(map)
    }

    /**
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage(if (flags==1) "wallpaper" else "painting",item.fontDrawId)
        val images= mutableListOf<String>()
        images.add(item.bodyUrl)
        var imageDownLoad= ImageDownLoadUtils(this,images.toTypedArray(),pathStr)
        imageDownLoad.startDownload()
        imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
            override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                hideLoading()
                val paths= mutableListOf<String>()
                if (map != null) {
                    for (m in map){
                        paths.add(m.value)
                    }
                }
                val bean= PaintingBean()
                bean.contentId=item.fontDrawId
                bean.type=flags
                bean.title=item.drawName
                bean.date=System.currentTimeMillis()
                bean.paths=paths
                bean.time=dynasty-1
                bean.timeStr=popTimes[dynasty-1].name
                bean.paintingType=paintingType-1
                bean.paintingTypeStr=popPaintings[paintingType-1].name
                bean.info=item.drawDesc
                bean.price=item.price
                bean.imageUrl=item.imageUrl
                bean.supply=item.supply
                PaintingBeanDaoManager.getInstance().insertOrReplace(bean)
            }

            override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                imageDownLoad.reloadImage()
            }

        })
    }


    /**
     * 朝代选择器
     */
    private fun selectorTime(){
        if (popWindowTime==null)
        {
            popWindowTime= PopWindowList(this,popTimes,tv_time,5).builder()
            popWindowTime?.setOnSelectListener { item ->
                tv_time.text=item.name
                dynasty=item.id
                pageIndex=1
                fetchData()
            }
        }
        else{
            popWindowTime?.show()
        }
    }

    /**
     * 书画选择器
     */
    private fun selectorPainting(){
        if (popWindowPainting==null)
        {
            popWindowPainting= PopWindowList(this,popPaintings,tv_painting_type,5).builder()
            popWindowPainting?.setOnSelectListener { item ->
                tv_painting_type.text=item.name
                paintingType=item.id
                pageIndex=1
                fetchData()
            }
        }
        else{
            popWindowPainting?.show()
        }
    }




}