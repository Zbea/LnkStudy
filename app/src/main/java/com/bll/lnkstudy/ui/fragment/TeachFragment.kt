package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.ui.activity.TeachListActivity
import com.bll.lnkstudy.ui.adapter.TeachCourseAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco2
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_teach.*

/**
 * 教学
 */
class TeachFragment : BaseFragment(){

    private var mAdapter:TeachCourseAdapter?=null
    private var courses= mutableListOf<CourseBean>()
    private var courseMap=HashMap<Int,MutableList<CourseBean>>()//将所有数据按12个分页
    private var pageIndex=1

    override fun getLayoutId(): Int {
        return R.layout.fragment_teach
    }

    override fun initView() {
        setPageTitle("义教")
        setDisBackShow()

        courses=DataBeanManager.getIncetance().courses


        rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
        mAdapter = TeachCourseAdapter(R.layout.item_teach_course, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco2(0,90))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            var bundle=Bundle()
            bundle.putSerializable("course",courses[position])
            startActivity(Intent(activity,TeachListActivity::class.java)
                .putExtra("bundleCourse",bundle))
        }

        pageNumberView()

    }

    override fun lazyLoad() {
    }


    //翻页处理
    private fun pageNumberView(){
        var pageTotal=courses.size
        var toIndex=6
        var pageCount=Math.ceil((pageTotal.toDouble()/toIndex)).toInt()
        for(i in 0 until pageCount){
            var index=i*6
            if(index+6>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = courses.subList(index,index+toIndex)
            courseMap[i+1]=newList
        }

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
        courses= courseMap[pageIndex]!!
        mAdapter?.setNewData(courses)
        tv_page_current.text=pageIndex.toString()
    }

}