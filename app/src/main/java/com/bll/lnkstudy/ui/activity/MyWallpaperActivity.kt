package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.WallpaperDaoManager
import com.bll.lnkstudy.mvp.model.WallpaperBean
import com.bll.lnkstudy.ui.adapter.MyWallpaperAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_wallpaper_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlin.math.ceil

class MyWallpaperActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<WallpaperBean>()
    private var mAdapter:MyWallpaperAdapter?=null
    private var pageIndex=1 //当前页码
    private var listMap=HashMap<Int,MutableList<WallpaperBean>>()

    override fun layoutId(): Int {
        return R.layout.ac_my_wallpaper_list
    }

    override fun initData() {
        lists=WallpaperDaoManager.getInstance(this).queryAll(0)
    }

    override fun initView() {
        setPageTitle("壁纸更换")

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = MyWallpaperAdapter(R.layout.item_my_wallpaper, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,19f),0))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val index=(pageIndex-1)* Constants.PAGE_SIZE+position
            if (view.id==R.id.cb_left){
                for (item in lists){
                    item.isLeft=false
                }
                lists[index].isLeft=true
            }
            if (view.id==R.id.cb_right){
                for (item in lists){
                    item.isRight=false
                }
                lists[index].isRight=true
            }
            mAdapter?.notifyDataSetChanged()
        }

        pageNumberView()

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=lists.size //全部数量
        var pageNum=12
        var pageCount= ceil((pageTotal.toDouble()/pageNum)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        var toIndex=pageNum
        for(i in 0 until pageCount){
            var index=i*pageNum
            if(index+pageNum>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = lists.subList(index,index+toIndex)
            listMap[i+1]=newList
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                upDateUI()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                upDateUI()
            }
        }

    }

    //刷新数据
    private fun upDateUI()
    {
        mAdapter?.setNewData(listMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}