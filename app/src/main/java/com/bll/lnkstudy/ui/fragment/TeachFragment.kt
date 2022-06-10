package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.ui.activity.TeachListActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.TeachCourseAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceGridItemDeco2
import kotlinx.android.synthetic.main.fragment_bookcase.*
import kotlinx.android.synthetic.main.fragment_teach.*
import kotlinx.android.synthetic.main.fragment_teach.rv_list

/**
 * 教学
 */
class TeachFragment : BaseFragment(){

    private var mAdapter:TeachCourseAdapter?=null


    override fun getLayoutId(): Int {
        return R.layout.fragment_teach
    }

    override fun initView() {
        setPageTitle("义教")
        setDisBackShow()

        val courses=DataBeanManager.getIncetance().courses

        rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
        mAdapter = TeachCourseAdapter(R.layout.item_teach_course, courses)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco2(0,60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity,TeachListActivity::class.java).putExtra("course",courses[position].name))
        }

    }

    override fun lazyLoad() {
    }




}