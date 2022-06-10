package com.bll.lnkstudy.ui.fragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.HomeWork
import com.bll.lnkstudy.ui.adapter.HomeWorkAdapter
import com.bll.lnkstudy.utils.PopWindowUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_homework.*


/**
 * 作业
 */
class HomeWorkFragment : BaseFragment(){

    private var popWindow:PopWindowUtil?=null
    private var mAdapter:HomeWorkAdapter?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        setPageTitle("作业")
        setDisBackShow()
        setShowHomework()

        ivHomework?.setOnClickListener {
            setTopSelectView()
        }

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
                var pos=tab?.position
                var data=when(pos){
                    0->{
                        getHomeworkType(0,1,2,3,4,6)
                    }
                    2->{
                        getHomeworkType(0,1,2,3,4)
                    }
                    3,4,8->{
                        getHomeworkType(0,1,2,3,5)
                    }
                    else->{
                        getHomeworkType(0,1,2,3)
                    }
                }
                mAdapter?.setNewData(data)

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }


    private fun initRecyclerView(){
        var datas=getHomeworkType(0,1,2,3,4,6) as List<HomeWork>?
        mAdapter = HomeWorkAdapter(R.layout.item_homework, datas)
        rv_list.layoutManager = GridLayoutManager(activity,3)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,90))

    }

    /**
     * 获取不同科目 不同作业分类集合
     */
    private fun getHomeworkType(vararg types: Int): List<HomeWork?>? {
        val list= mutableListOf<HomeWork>()
        val homeWork = HomeWork()
        homeWork.title = "随堂作业本"
        homeWork.isPg = true
        homeWork.type = 0
        homeWork.resId = R.mipmap.icon_homework_st
        list.add(homeWork)
        val homeWork1 = HomeWork()
        homeWork1.title = "课件作业集"
        homeWork1.type = 1
        homeWork1.resId = R.mipmap.icon_homework_kj
        list.add(homeWork1)
        val homeWork2 = HomeWork()
        homeWork2.title = "家庭作业本"
        homeWork2.type = 2
        homeWork2.resId = R.mipmap.icon_homework_jt
        list.add(homeWork2)
        val homeWork4 = HomeWork()
        homeWork4.title = "我的课辅本"
        homeWork4.type = 3
        homeWork4.resId = R.mipmap.icon_homework_kf
        list.add(homeWork4)
        val homeWork5 = HomeWork()
        homeWork5.title = "朗读作业本"
        homeWork5.type = 4
        homeWork5.resId = R.mipmap.icon_homework_kf
        list.add(homeWork5)
        val homeWork6 = HomeWork()
        homeWork6.title = "实验报告"
        homeWork6.type = 5
        homeWork6.resId = R.mipmap.icon_homework_pg
        list.add(homeWork6)
        val homeWork7 = HomeWork()
        homeWork7.title = "社会实践"
        homeWork7.type = 6
        homeWork7.resId = R.mipmap.icon_homework_sj
        list.add(homeWork7)

        var homeWorks= mutableListOf<HomeWork>()
        for (i in types)
        {
            homeWorks.add(list[i])
        }

        return homeWorks
    }



    //顶部弹出选择
    private fun setTopSelectView(){
        if (popWindow==null){
            val popView = LayoutInflater.from(activity).inflate(R.layout.popwindow_homework, null, false)
            val llTj=popView?.findViewById<LinearLayout>(R.id.ll_tj)
            val ivTj=popView?.findViewById<ImageView>(R.id.iv_select_tj)
            val llPg=popView?.findViewById<LinearLayout>(R.id.ll_pg)
            val ivPg=popView?.findViewById<ImageView>(R.id.iv_select_pg)
            llTj?.setOnClickListener {
                ivTj?.visibility=View.VISIBLE
                ivPg?.visibility=View.GONE
                popWindow?.dismiss()
            }
            llPg?.setOnClickListener {
                ivTj?.visibility=View.GONE
                ivPg?.visibility=View.VISIBLE
                popWindow?.dismiss()
            }
            popWindow=PopWindowUtil.getInstance().makePopupWindow(activity,ivHomework,popView, -160,20, Gravity.LEFT)
            popWindow?.show()
        }
        else{
            popWindow?.show()
        }
    }


}