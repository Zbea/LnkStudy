package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
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
import kotlinx.android.synthetic.main.ac_teach_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlin.math.ceil

class TeachListActivity:BaseAppCompatActivity(),IContractView.ITeachingVideoView {

    private val mPresenter=TeachingVideoPresenter(this)
    private var flags=0
    private var item:ItemList?=null
    private var type:TeachingVideoType?=null
    private var grade=0//年级
    private var semester=0//学期
    private var grades= mutableListOf<PopupBean>()
    private var semesters= mutableListOf<PopupBean>()
    private var datas= mutableListOf<TeachingVideoList.ItemBean>()
    private var mAdapter:TeachListAdapter?=null
    private var pageIndex=1 //当前页码
    private var pageCount=1
    private val pageSize=20

    private var popWindowGrade:PopupList?=null
    private var popWindowSemester:PopupList?=null

    override fun onList(list: TeachingVideoList?) {
        pageCount = ceil(list?.total?.toDouble()!! / pageSize).toInt()
        val totalTotal = list?.total
        if (totalTotal == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        datas=list?.list
        mAdapter?.setNewData(datas)

    }
    override fun onCourse(type: TeachingVideoType?) {
    }

    override fun onType(type: TeachingVideoType?) {

    }

    override fun layoutId(): Int {
        return R.layout.ac_teach_list
    }

    override fun initData() {
        flags=intent.flags
        item= intent.getBundleExtra("bundle").getSerializable("item") as ItemList
        type=intent.getBundleExtra("bundle").getSerializable("type") as TeachingVideoType

        val gradeItems=type?.grades!!
        if (gradeItems.size>0){
            for (i in gradeItems.indices){
                grades.add(PopupBean(gradeItems[i].type,gradeItems[i].desc,i==0))
            }
            grade=grades[0].id
            tv_grade.text = grades[0].name
        }

        if (flags==0){
            val semesterItems=type?.semesters!!
            if (semesterItems.size>0){
                for (i in semesterItems.indices){
                    semesters.add(PopupBean(semesterItems[i].type,semesterItems[i].desc,i==0))
                }
                semester=semesters[0].id
                tv_semester.text = semesters[0].name
            }
        }

        setFetchData()
    }

    private fun setFetchData(){
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

    override fun initView() {
        setPageTitle(item?.desc!!)
        if (flags==0) showView(tv_grade,tv_semester) else showView(tv_grade)

        rv_list.layoutManager = GridLayoutManager(this,5)//创建布局管理
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

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                setFetchData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                setFetchData()
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
                    setFetchData()
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
                    setFetchData()
                }
            } else {
                popWindowSemester?.show()
            }
        }
    }


}