package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.mvp.model.TestPaperType
import com.bll.lnkstudy.ui.activity.TestPaperListActivity
import com.bll.lnkstudy.ui.adapter.TestPaperTypeAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco2
import kotlinx.android.synthetic.main.common_xtab.*
import kotlinx.android.synthetic.main.fragment_testpaper.*

/**
 * 考卷
 */
class TestPaperFragment : BaseFragment(){

    private var mAdapter:TestPaperTypeAdapter?=null
    private var items= mutableListOf<TestPaperType>()
    private var course:CourseBean?=null//课程id

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setPageTitle("考卷")
        setDisBackShow()

        initData()
        initRecyclerView()
        initTab()

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        val courses=DataBeanManager.getIncetance().courses
        course=courses[0]

        for (item in courses){
            xtab?.newTab()?.setText(item.name)?.let { it -> xtab?.addTab(it) }
        }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                course=courses[tab?.position!!]
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }

    private fun initData(){
        var testPaper= TestPaperType()
        testPaper.isPg=true
        testPaper.name="单元测试卷"
        testPaper.namePath="dy"
        testPaper.type=0
        testPaper.resId=R.mipmap.icon_testpaper_dy
        items.add(testPaper)

        var testPaper3=TestPaperType()
        testPaper3.isPg=true
        testPaper3.resId=R.mipmap.icon_testpaper_jd
        testPaper3.name="阶段考试卷"
        testPaper3.namePath="jd"
        testPaper3.type=1
        items.add(testPaper3)

        var testPaper4=TestPaperType()
        testPaper4.isPg=true
        testPaper4.resId=R.mipmap.icon_testpaper_xq
        testPaper4.name="学期考试卷"
        testPaper4.namePath="xq"
        testPaper4.type=2
        items.add(testPaper4)
    }

    private fun initRecyclerView(){

        mAdapter = TestPaperTypeAdapter(R.layout.item_testpaper_type,items)
        rv_list.layoutManager = GridLayoutManager(activity,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco2(0,80))
        mAdapter?.setOnItemClickListener { adapter, view, position ->

            var bundle=Bundle()
            bundle.putSerializable("course",course)
            bundle.putSerializable("testPaperType",items[position])

            startActivity(Intent(activity,TestPaperListActivity::class.java).putExtra("testPaper",bundle))

        }

    }




}