package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.TeachList
import com.bll.lnkstudy.ui.activity.date.MainDatePlanDetailsActivity
import com.bll.lnkstudy.ui.adapter.TeachListAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_bookcase_mycollect.*

class TeachListActivity:BaseActivity() {

    private var courseStr=""
    private var teachs= mutableListOf<TeachList>()
    private var mAdapter:TeachListAdapter?=null
    private var pageIndex=1 //当前页码
    private var bookMap=HashMap<Int,MutableList<TeachList>>()//将所有数据按30个分页

    override fun layoutId(): Int {
        return R.layout.ac_teach_list
    }

    override fun initData() {
        courseStr= intent.getStringExtra("course").toString()

        for (index in 0..40){
            var item=TeachList()
            item.name="三角函数"
            item.info="北京大学 张老师"
            teachs.add(item)
        }

    }

    override fun initView() {
        setPageTitle(courseStr)

        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = TeachListAdapter(R.layout.item_teach_content, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->

            val intent= Intent(this, TeachActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("teach", teachs[position])
            intent.putExtra("teachs", bundle)
            startActivity(intent)

        }

        pageNumberView()

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=teachs.size //全部数量
        var pageCount=Math.ceil((pageTotal.toDouble()/30)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        var toIndex=30
        for(i in 0 until pageCount){
            var index=i*30
            if(index+30>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = teachs.subList(index,index+toIndex)
            bookMap[i+1]=newList
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
        mAdapter?.setNewData(bookMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}