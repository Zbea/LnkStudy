package com.bll.lnkstudy.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.TestPaper
import com.bll.lnkstudy.ui.adapter.TestPaperAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_homework.*

/**
 * 考卷
 */
class TestPaperFragment : BaseFragment(){

    private var mAdapter:TestPaperAdapter?=null
    private var items= mutableListOf<TestPaper>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
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

        for (item in courses){
            xtab?.newTab()?.setText(item.name)?.let { it -> xtab?.addTab(it) }
        }

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(tab?.text.toString()){
                    "语文" -> {

                    }
                    else -> {

                    }
                }
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }

    private fun initData(){
        var testPaper=TestPaper()
        testPaper.isPg=true
        testPaper.resId=R.mipmap.icon_testpaper_dy
        items.add(testPaper)

        var testPaper1=TestPaper()
        testPaper1.resId=R.mipmap.icon_testpaper_qz
        items.add(testPaper1)

        var testPaper2=TestPaper()
        testPaper2.resId=R.mipmap.icon_testpaper_end
        items.add(testPaper2)

        var testPaper3=TestPaper()
        testPaper3.isPg=true
        testPaper3.resId=R.mipmap.icon_testpaper_jd
        items.add(testPaper3)

        var testPaper4=TestPaper()
        testPaper4.isPg=true
        testPaper4.resId=R.mipmap.icon_testpaper_mn
        items.add(testPaper4)
    }

    private fun initRecyclerView(){

        mAdapter = TestPaperAdapter(R.layout.item_testpaper,items)
        rv_list.layoutManager = GridLayoutManager(activity,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,80))

    }




}