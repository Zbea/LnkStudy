package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.ui.adapter.TeachListAdapter
import kotlinx.android.synthetic.main.ac_teach_list.*
import kotlinx.android.synthetic.main.common_page_number.*

class TeachListActivity:BaseAppCompatActivity() {

    private var course:CourseBean?=null
    private var teachs= mutableListOf<ItemList>()
    private var mAdapter:TeachListAdapter?=null
    private var pageIndex=1 //当前页码
    private var pageTotal=1


    override fun layoutId(): Int {
        return R.layout.ac_teach_list
    }

    override fun initData() {
        course= intent.getBundleExtra("bundleCourse")?.getSerializable("course") as CourseBean

        for (index in 0..40){
            val item= ItemList()
            item.id=index
            item.name="三角函数"
            item.info="北京大学 张老师"
            teachs.add(item)
        }

    }

    override fun initView() {
        setPageTitle(course?.name.toString())

        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = TeachListAdapter(R.layout.item_teach_content, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->

            val intent= Intent(this, TeachActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("teach", teachs[position])
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)

        }

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1

            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1

            }
        }

    }


}