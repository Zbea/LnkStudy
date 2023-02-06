package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.ui.adapter.MyWallpaperAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_wallpaper_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import java.io.File
import kotlin.math.ceil

class MyWallpaperActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyWallpaperAdapter?=null
    private var pageIndex=1 //当前页码
    private var pageCount=1
    private var listMap=HashMap<Int,MutableList<PaintingBean>>()

    override fun layoutId(): Int {
        return R.layout.ac_my_wallpaper_list
    }

    override fun initData() {
        lists= PaintingBeanDaoManager.getInstance().queryWallpapers()
    }

    override fun initView() {
        setPageTitle("壁纸更换")

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = MyWallpaperAdapter(R.layout.item_my_wallpaper, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,19f),0))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            ImageDialog(this, File(lists[position].paths[0])).builder()
        }
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

        tv_ok.setOnClickListener {
            var leftPath=""
            var rightPath=""
            for (item in lists){
                if (item.isLeft){
                    leftPath=item.paths[0]
                }
                if (item.isRight){
                    rightPath=item.paths[0]
                }
            }
        }

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

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=lists.size //全部数量
        pageCount= ceil(pageTotal.toDouble()/Constants.PAGE_SIZE).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        var toIndex=Constants.PAGE_SIZE
        for(i in 0 until pageCount){
            var index=i*Constants.PAGE_SIZE
            if(index+Constants.PAGE_SIZE>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = lists.subList(index,index+toIndex)
            listMap[i+1]=newList
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()


    }

    //刷新数据
    private fun upDateUI()
    {
        mAdapter?.setNewData(listMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}