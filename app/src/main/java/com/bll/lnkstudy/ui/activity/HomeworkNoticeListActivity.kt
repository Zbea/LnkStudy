package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.presenter.HomeworkNoticePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.HomeworkNoticeAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list

/**
 * 主页作业通知
 */
class HomeworkNoticeListActivity:BaseAppCompatActivity(),IContractView.IHomeworkNoticeView {

    private lateinit var mPresenter:HomeworkNoticePresenter
    private var flags=0
    private var mAdapter: HomeworkNoticeAdapter?=null


    override fun onHomeworkNotice(list: HomeworkNoticeList) {
        setPageNumber(list.total)
        mAdapter?.setNewData(list.list)
    }
    override fun onCorrect(list: HomeworkNoticeList) {
        setPageNumber(list.total)
        mAdapter?.setNewData(list.list)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        flags=intent.flags
        initChangeScreenData()

    }

    override fun initChangeScreenData() {
        mPresenter = HomeworkNoticePresenter(this, getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(if (flags==0) "作业通知" else "批改通知")

        initRecyclerView()

        fetchData()
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@HomeworkNoticeListActivity,50f),
            DP2PX.dip2px(this@HomeworkNoticeListActivity,30f),
            DP2PX.dip2px(this@HomeworkNoticeListActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkNoticeAdapter(R.layout.item_homework_notice, flags,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
        }
    }

    override fun fetchData() {
       val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        if (flags==0){
            mPresenter.getHomeworkNotice(map)
        }
        else{
            mPresenter.getCorrectNotice(map)
        }
    }

}