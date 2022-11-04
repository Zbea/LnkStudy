package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.manager.WallpaperDaoManager
import com.bll.lnkstudy.mvp.model.AppListBean
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.mvp.model.WallpaperBean
import com.bll.lnkstudy.ui.adapter.AppWallpaperListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_app_official.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*

class AppOfficialActivity:BaseAppCompatActivity(){

    private var flags=0
    private var items= mutableListOf<AppListBean.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var mAdapter:AppWallpaperListAdapter?=null

    private var popTimes= mutableListOf<PopWindowBean>()
    private var popPaintings= mutableListOf<PopWindowBean>()
    private var popWindowTime:PopWindowList?=null
    private var popWindowPainting:PopWindowList?=null
    private var position=0

    private var time=0 //年代
    private var paintingType=0 //书画内容


    override fun layoutId(): Int {
        return R.layout.ac_app_official
    }

    override fun initData() {
        flags=intent.flags

        for (i in 1..12){
            val item =AppListBean.ListBean()
            item.id=i
            item.name="标题$i"
            item.status=1
            item.price=i
            items.add(item)
        }

        val yeas= DataBeanManager.getIncetance().YEARS
        for (i in yeas.indices){
            popTimes.add(PopWindowBean(i,yeas[i],i==0))
        }

        val paintings= DataBeanManager.getIncetance().PAINTING
        for (i in paintings.indices){
            popPaintings.add(PopWindowBean(i,paintings[i],i==0))
        }

    }

    override fun initView() {
        setPageTitle(if (flags==0) "官方壁纸" else "官方书画")
        ll_painting.visibility=if (flags==0) View.GONE else View.VISIBLE

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

                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1

            }
        }

    }

    private fun initRecyclerView(){

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = AppWallpaperListAdapter(R.layout.item_app_wallpaper, items,flags)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),30))
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener{ adapter, view, position ->
            if (view.id==R.id.btn_download){
                this.position=position
                onDownload()
            }
        }
    }

    /**
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage(if (flags==0) "wallpaper" else "painting",item.id)
        var imageDownLoad= ImageDownLoadUtils(this,item.images,pathStr)
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
                val bean=WallpaperBean()
                bean.contentId=item.id
                bean.type=flags
                bean.title=item.name
                bean.date=System.currentTimeMillis()
                bean.paths=paths
                bean.time=time
                bean.timeStr=item.timeStr
                bean.paintingType=paintingType
                bean.paintingTypeStr=item.paintingTypeStr
                bean.info=item.introduction
                bean.price=item.price
                bean.imageUrl=item.assetUrl
                WallpaperDaoManager.getInstance(this@AppOfficialActivity).insertOrReplace(bean)
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
                time=item.id
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
            }
        }
        else{
            popWindowPainting?.show()
        }
    }


}