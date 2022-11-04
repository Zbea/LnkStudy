package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.ui.activity.TeachListActivity
import com.bll.lnkstudy.ui.adapter.TeachCourseAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_teach.*

/**
 * 教学
 */
class TeachFragment : BaseFragment() {

    private var mAdapter: TeachCourseAdapter? = null
    private var courses = mutableListOf<CourseBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teach
    }

    override fun initView() {
        setTitle("义教")

        courses = DataBeanManager.getIncetance().courses

        initTab()

        rv_list.layoutManager = GridLayoutManager(activity, 2)//创建布局管理
        mAdapter = TeachCourseAdapter(R.layout.item_teach_course, DataBeanManager.getIncetance().kmArray.asList())
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco(0, DP2PX.dip2px(activity, 50f)))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            var bundle = Bundle()
            bundle.putSerializable("course", courses[position])
            customStartActivity(
                Intent(activity, TeachListActivity::class.java)
                    .putExtra("bundleCourse", bundle)
            )
        }

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {

        val teachList = DataBeanManager.getIncetance().teachList

        for (i in teachList.indices) {
            rg_group.addView(getRadioButton(i ,teachList[i],teachList.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().kmArray.asList())
                }
                1 -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().teachTYList.asList())
                }
                2 -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().teachMSList.asList())
                }
                3 -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().teachWDList.asList())
                }
                4 -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().teachYJList.asList())
                }
                else -> {
                    mAdapter?.setNewData(DataBeanManager.getIncetance().teachBCList.asList())
                }
            }
        }

    }


}