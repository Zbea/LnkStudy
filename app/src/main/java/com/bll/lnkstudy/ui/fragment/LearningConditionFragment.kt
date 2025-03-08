package com.bll.lnkstudy.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.presenter.HomeworkNoticePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.CorrectDetailsAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_app_list.rv_list
import kotlinx.android.synthetic.main.common_fragment_title.tv_course

class LearningConditionFragment:BaseMainFragment(), IContractView.IHomeworkNoticeView {
    private var mPresenter: HomeworkNoticePresenter = HomeworkNoticePresenter(this, 1)
    private var mAdapter: CorrectDetailsAdapter?=null
    private var coursePops= mutableListOf<PopupBean>()
    private var currentCourses= mutableListOf<String>()
    private var mCourse=""
    private var type=1

    override fun onCorrect(list: HomeworkNoticeList) {
        setPageNumber(list.total)
        mAdapter?.setNewData(list.list)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[8])
        pageSize=14

        tv_course.setOnClickListener {
            PopupList(requireActivity(), coursePops, tv_course, tv_course.width, 5).builder()
                .setOnSelectListener { item ->
                    mCourse=item.name
                    tv_course.text = mCourse
                    pageIndex = 1
                    fetchData()
                }
        }

        initRecyclerView()
        initTab()
        initType()
    }

    override fun lazyLoad() {
    }

    private fun initType(){
        if (MethodManager.getCourses().isNotEmpty()&&currentCourses!= MethodManager.getCourses()){
            currentCourses= MethodManager.getCourses()
            coursePops.clear()
            mCourse=currentCourses[0]
            for (course in currentCourses){
                coursePops.add(PopupBean(currentCourses.indexOf(course),course,currentCourses.indexOf(course)==0))
            }
            showView(tv_course)
            tv_course.text=mCourse
            pageIndex=1
            fetchData()
        }
    }

    private fun initTab(){
        val strs= arrayListOf("作业批改","考卷批改")
        for (str in strs) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=str
                isCheck=strs.indexOf(str)==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        type=position+1
        pageIndex = 1
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(requireActivity())//创建布局管理
        mAdapter = CorrectDetailsAdapter(R.layout.item_correct_details, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            ImageDialog(requireActivity(),1,mAdapter?.getItem(position)?.correctUrl!!.split(",")).builder()
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=mAdapter?.getItem(position)
            if (view.id==R.id.tv_answer){
                ImageDialog(requireActivity(),1,item!!.answerUrl.split(",")).builder()
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(20))
    }

    override fun fetchData() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            val map=HashMap<String,Any>()
            map["page"]=pageIndex
            map["size"]=pageSize
            map["subject"]=DataBeanManager.getCourseId(mCourse)
            map["taskType"]=type
            mPresenter.getCorrectNotice(map)
        }
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                initType()
            }
        }
    }

}