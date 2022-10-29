package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.PATH_SF
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.WallpaperDaoManager
import com.bll.lnkstudy.mvp.model.WallpaperBean
import com.bll.lnkstudy.ui.adapter.MyPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_painting_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlin.math.ceil

class MyPaintingListActivity:BaseAppCompatActivity() {

    private var titleStr=""
    private var type=0 //type=0 type=1书法
    private var time=0
    private var paintingType=0
    private var lists= mutableListOf<WallpaperBean>()
    private var mAdapter:MyPaintingAdapter?=null
    private var pageIndex=1 //当前页码
    private var pageTotal=1

    override fun layoutId(): Int {
        return R.layout.ac_my_painting_list
    }

    override fun initData() {
        titleStr= intent.getStringExtra("title").toString()
        time=intent.getIntExtra("time",0)
        paintingType=intent.getIntExtra("paintingType",0)
        type=intent.flags

    }

    override fun initView() {
        setPageTitle(titleStr)
        showSearchView(true)

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = MyPaintingAdapter(R.layout.item_painting, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,19f),0))


        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1
                findData()
            }
        }
        findData()
    }

    private fun findData(){

        if (type==0){
            lists=WallpaperDaoManager.getInstance(this).queryAllPainting(1,time,paintingType,pageIndex,Constants.PAGE_SIZE)
            val total= WallpaperDaoManager.getInstance(this).queryAllPainting(1,time,paintingType)
            pageTotal= ceil(((total/ Constants.PAGE_SIZE).toDouble())).toInt()
        }
        if (type==1){
            var path=assets.list("sf")
            if (path != null) {
                for (s in path){
                    var item=WallpaperBean()
                    item.imageUrl=PATH_SF+s
                    lists.add(item)
                }
            }
        }
        mAdapter?.setNewData(lists)
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageTotal.toString()
    }


}