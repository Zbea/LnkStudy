package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.ui.adapter.MyPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_painting_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import java.io.File
import kotlin.math.ceil

class MyPaintingListActivity:BaseAppCompatActivity() {

    private var titleStr=""
    private var type=0 //type=0 type=1书法
    private var time=0
    private var paintingType=0
    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyPaintingAdapter?=null
    private var pageIndex=1 //当前页码
    private var pageTotal=1
    private val pageSize=6

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
        mAdapter = MyPaintingAdapter(R.layout.item_download_painting, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@MyPaintingListActivity,2)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this@MyPaintingListActivity,30f),130))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@MyPaintingListActivity,File(lists[position].paths[0])).builder()
            }
        }

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
            lists=PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType,pageIndex,pageSize)
            val total= PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType)
            pageTotal= ceil(total.toDouble()/ pageSize).toInt()
        }
        else{
            lists= PaintingBeanDaoManager.getInstance().queryPaintings(paintingType,pageIndex,pageSize)
            val total= PaintingBeanDaoManager.getInstance().queryPaintings(paintingType)
            pageTotal= ceil(total.toDouble()/ pageSize).toInt()
        }

        mAdapter?.setNewData(lists)
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageTotal.toString()
        ll_page_number.visibility=if (pageTotal==0) View.GONE else View.VISIBLE
    }


}