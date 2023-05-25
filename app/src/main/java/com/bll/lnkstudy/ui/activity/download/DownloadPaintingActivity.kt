package com.bll.lnkstudy.ui.activity.download

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.mvp.model.PaintingList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.presenter.DownloadPaintingPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.DownloadPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_download_app.*
import kotlinx.android.synthetic.main.common_title.*

class DownloadPaintingActivity:BaseAppCompatActivity(),IContractView.IPaintingView{

    private val presenter=DownloadPaintingPresenter(this)
    private var items= mutableListOf<PaintingList.ListBean>()
    private var mAdapter:DownloadPaintingAdapter?=null

    private var popTimes= mutableListOf<PopupBean>()
    private var popPaintings= mutableListOf<PopupBean>()
    private var popWindowTime:PopupList?=null
    private var popWindowPainting:PopupList?=null
    private var position=0

    private var supply=1//官方
    private var dynasty=0 //年代
    private var paintingType=0 //书画内容

    override fun onList(bean: PaintingList) {
        setPageNumber(bean.total)
        items=bean.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
        items[position].buyStatus=1
        mAdapter?.notifyDataSetChanged()
    }

    override fun layoutId(): Int {
        return R.layout.ac_download_app
    }

    override fun initData() {
        pageSize=6
        popTimes=DataBeanManager.popupDynasty()
        dynasty=popTimes[0].id

        popPaintings=DataBeanManager.popupPainting()
        paintingType=popPaintings[0].id

        fetchData()
    }

    override fun initView() {
        setPageTitle(R.string.download_painting)
        showView(tv_dynasty,tv_painting_type)

        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            supply = if (id==R.id.rb_official){
                1
            }else{
                2
            }
            pageIndex=1
            fetchData()
        }

        initRecyclerView()

        tv_dynasty.text=popTimes[0].name
        tv_dynasty.setOnClickListener {
            selectorTime()
        }
        tv_painting_type.text=popPaintings[0].name
        tv_painting_type.setOnClickListener {
            selectorPainting()
        }
    }

    private fun initRecyclerView(){
        val distance=DP2PX.dip2px(this,30f)
        rv_list.layoutManager = GridLayoutManager(this,2)//创建布局管理
        mAdapter = DownloadPaintingAdapter(R.layout.item_download_painting, items)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco1(2,distance,100))
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(distance,DP2PX.dip2px(this,50f),distance,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
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
                        showToast(R.string.toast_downloaded)
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
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage("painting" ,item.fontDrawId)
        val images= mutableListOf<String>()
        images.add(item.bodyUrl)
        val imageDownLoad= ImageDownLoadUtils(this,images.toTypedArray(),pathStr)
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
                bean.type=2
                bean.title=item.drawName
                bean.date=System.currentTimeMillis()
                bean.paths=paths
                bean.time=dynasty
                bean.timeStr=popTimes[dynasty-1].name
                bean.paintingType=paintingType
                bean.paintingTypeStr=popPaintings[paintingType-1].name
                bean.info=item.drawDesc
                bean.price=item.price
                bean.imageUrl=item.imageUrl
                bean.author=item.author
                bean.supply=item.supply
                bean.bodyUrl=item.bodyUrl
                val id=PaintingBeanDaoManager.getInstance().insertOrReplaceGetId(bean)
                //新建增量更新
                DataUpdateManager.createDataUpdateSource(7,id.toInt(),1,bean.contentId, Gson().toJson(bean),item.bodyUrl)
            }
            override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                hideLoading()
                showToast(R.string.book_download_fail)
            }
        })
    }


    /**
     * 朝代选择器
     */
    private fun selectorTime(){
        if (popWindowTime==null)
        {
            popWindowTime= PopupList(this,popTimes,tv_dynasty,tv_dynasty.width,5).builder()
            popWindowTime?.setOnSelectListener { item ->
                tv_dynasty.text=item.name
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
            popWindowPainting= PopupList(this,popPaintings,tv_painting_type,tv_painting_type.width,5).builder()
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

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["supply"]=supply
        map["type"]=2
        if (paintingType!=5&&paintingType!=6){
            map["dynasty"] =dynasty
        }
        map["subType"]=paintingType
        presenter.getList(map)
    }


}