package com.bll.lnkstudy.ui.activity.download

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.mvp.model.PaintingList
import com.bll.lnkstudy.mvp.presenter.DownloadPaintingPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.DownloadWallpaperAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_download_app.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlin.math.ceil

class DownloadWallpaperActivity:BaseAppCompatActivity(),IContractView.IPaintingView{

    private val presenter=DownloadPaintingPresenter(this)
    private var items= mutableListOf<PaintingList.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var pageSize=12
    private var mAdapter:DownloadWallpaperAdapter?=null
    private var supply=1//官方
    private var position=0

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
        return R.layout.ac_download_app
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle("壁纸")

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

        fetchData()

    }

    private fun initRecyclerView(){

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = DownloadWallpaperAdapter(R.layout.item_download_wallpaper, items)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),30))
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,28f),DP2PX.dip2px(this,50f),DP2PX.dip2px(this,28f),0)
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
        map["supply"]=supply
        map["type"]=1
        presenter.getList(map)
    }

    /**
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage("wallpaper",item.fontDrawId)
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
                bean.type=1
                bean.title=item.drawName
                bean.date=System.currentTimeMillis()
                bean.paths=paths
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
}