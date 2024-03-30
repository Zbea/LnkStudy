package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.presenter.TeachingVideoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TeachListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*

class TeachListActivity:BaseAppCompatActivity(),IContractView.ITeachingVideoView {

    private lateinit var mPresenter:TeachingVideoPresenter
    private var flags=0
    private var item:ItemList?=null
    private var grade=0//年级
    private var semester=0//学期
    private var grades= mutableListOf<PopupBean>()
    private var semesters= mutableListOf<PopupBean>()
    private var datas= mutableListOf<TeachingVideoList.ItemBean>()
    private var mAdapter:TeachListAdapter?=null

    private var popWindowGrade:PopupList?=null
    private var popWindowSemester:PopupList?=null

    override fun onList(list: TeachingVideoList) {
        setPageNumber(list.total)
        datas=list.list
        mAdapter?.setNewData(datas)

    }
    override fun onType(type: TeachingVideoType?) {
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=20
        flags=intent.flags
        item= intent.getBundleExtra("bundle")?.getSerializable("item") as ItemList

        if (flags==0){
            grade=mUser?.grade!!
            grades=DataBeanManager.popupGrades(grade)
        }
        else{
            grades=DataBeanManager.popupTypeGrades
            grade=grades[0].id
        }

        tv_grade.text = grades[grade-1].name

        if (flags==0){
            semesters=DataBeanManager.popupSemesters()
            semester=semesters[0].id
            tv_semester.text = semesters[0].name
        }
        if (NetworkUtil(this).isNetworkConnected()){
            fetchData()
        }
        else{
            showNetworkDialog()
        }
    }

    override fun initChangeScreenData() {
        mPresenter=TeachingVideoPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(item?.desc!!)
        if (flags==0) showView(tv_grade,tv_semester) else showView(tv_grade)

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, 20f),
            DP2PX.dip2px(this, 40f),
            DP2PX.dip2px(this, 20f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams
        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = TeachListAdapter(R.layout.item_teach_content, null).apply {
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val intent= Intent(this@TeachListActivity, TeachActivity::class.java)
                val bundle= Bundle()
                bundle.putSerializable("teach", datas[position])
                intent.putExtra("bundle", bundle)
                customStartActivity(intent)
            }
        }

        initSelectorView()
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {
        tv_grade.setOnClickListener {
            if (popWindowGrade == null) {
                popWindowGrade = PopupList(this, grades, tv_grade, tv_grade.width, 5).builder()
                popWindowGrade?.setOnSelectListener { item ->
                    grade = item.id
                    tv_grade.text = item.name
                    pageIndex = 1
                    fetchData()
                }
            } else {
                popWindowGrade?.show()
            }
        }

        tv_semester.setOnClickListener {
            if (popWindowSemester == null) {
                popWindowSemester = PopupList(this, semesters, tv_semester, tv_semester.width, 5).builder()
                popWindowSemester?.setOnSelectListener { item ->
                    semester=item.id
                    tv_semester.text = item.name
                    pageIndex = 1
                    fetchData()
                }
            } else {
                popWindowSemester?.show()
            }
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["grade"] = grade
        if (flags==0){
            map["type"] = item?.type!!
            map["semester"] = semester
            mPresenter.getCourseList(map)
        }
        else{
            map["subType"] = item?.type!!
            mPresenter.getList(map)
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeNetwork()
    }

}