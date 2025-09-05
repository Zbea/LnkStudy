package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.presenter.TeachingVideoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.VideoActivity
import com.bll.lnkstudy.ui.adapter.TeachListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SToast
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade
import kotlinx.android.synthetic.main.common_fragment_title.tv_semester
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list

/**
 * 教学
 */
class VideoFragment : BaseMainFragment(),IContractView.ITeachingVideoView {

    private val mPresenter=TeachingVideoPresenter(this,1)
    private var mAdapter: TeachListAdapter? = null
    private var mCourse=""
    private var semester=1

    override fun onList(list: TeachingVideoList) {
        setPageNumber(list.total)
        mAdapter?.setNewData(list.list)
        hideLoading()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        showView(tv_grade,tv_semester)
        setTitle(DataBeanManager.listTitle[7])
        pageSize=16
        initDialog(1)

        grade=mUser?.grade!!

        tv_grade.text=DataBeanManager.getGradeStr(grade)
        tv_grade.setOnClickListener {
            PopupList(requireActivity(), DataBeanManager.popupGrades(grade), tv_grade, tv_grade.width, 5).builder().setOnSelectListener { item ->
                grade = item.id
                tv_grade.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        semester=MethodManager.getSemester()
        tv_semester.text=DataBeanManager.popupSemesters()[semester-1].name
        tv_semester.setOnClickListener {
            PopupList(requireActivity(), DataBeanManager.popupSemesters(semester), tv_semester, tv_semester.width, 5).builder().setOnSelectListener { item ->
                semester=item.id
                tv_semester.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        initRecyclerView()

    }

    override fun lazyLoad() {
        if (itemTabTypes.isEmpty()){
            initTab()
        }
        else{
            fetchData()
        }
    }

    //设置头部索引
    private fun initTab() {
        setTabCourse()
        if (itemTabTypes.isNotEmpty()){
            mCourse=itemTabTypes[0].title
            fetchData()
        }
    }

    override fun onTabClickListener(view: View, position: Int) {
        mCourse=itemTabTypes[0].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 4)
        mAdapter = TeachListAdapter(R.layout.item_teach_content, null).apply {
            //创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { _, _, position ->
                if (MethodManager.isClassGroupPermissionArrow()){
                    if (NetworkUtil.isNetworkConnected()){
                        val intent= Intent(requireActivity(), VideoActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("teach", mAdapter?.data?.get(position))
                        intent.putExtra("bundle", bundle)
                        customStartActivity(intent)
                    }
                    else{
                        SToast.showTextLong(1,"WIFI未连接，无法播放视教")
                    }
                    return@setOnItemClickListener
                }
                if (!MethodManager.getSchoolPermissionAllow(1)){
                    showToast(1,"学校不允许该时间段播放视教")
                    return@setOnItemClickListener
                }
                if (!MethodManager.getParentPermissionAllow(1)){
                    showToast(1,"家长不允许该时间段播放视教")
                    return@setOnItemClickListener
                }
                if (!DateUtils.isTimeBetween7And22()) {
                    showToast(1, "该时间无法查看书籍")
                    return@setOnItemClickListener
                }
                if (NetworkUtil.isNetworkConnected()){
                    val intent= Intent(requireActivity(), VideoActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("teach", mAdapter?.data?.get(position))
                    intent.putExtra("bundle", bundle)
                    customStartActivity(intent)
                }
                else{
                    SToast.showTextLong(1,"WIFI未连接，无法播放视教")
                }
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(activity, 20f)))
    }

    override fun fetchData() {
        if (NetworkUtil.isNetworkConnected()){
            val map=HashMap<String,Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"] = grade
            map["type"] = DataBeanManager.getCourseId(mCourse)
            map["semester"] = semester
            mPresenter.getCourseList(map)
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onNetworkConnectionSuccess() {
        lazyLoad()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                initTab()
            }
        }
    }
}