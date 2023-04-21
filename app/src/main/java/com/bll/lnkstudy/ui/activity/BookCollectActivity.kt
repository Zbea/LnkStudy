package com.bll.lnkstudy.ui.activity

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.MainList
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICommonView
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.cloud.*
import kotlinx.android.synthetic.main.ac_cloud_storage.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 云存储
 */
class BookCollectActivity: BaseAppCompatActivity() ,ICommonView{

    private var mCommonPresenter= CommonPresenter(this)

    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var mData= mutableListOf<MainList>()
    private var lastFragment: Fragment? = null

    private var bookcaseFragment: CloudBookCaseFragment? = null
    private var textbookFragment: CloudTextbookFragment? = null
    private var paperFragment: CloudExamFragment? = null
    private var homeworkFragment: CloudHomeworkFragment? = null
    private var noteFragment: CloudNoteFragment? = null
    private var paintingFragment: CloudPaintingFragment? = null

    private var grade=mUser?.grade!!
    private var popWindowDynasty:PopupList?=null
    private var popWindowGrade:PopupList?=null

    override fun onList(commonData: CommonData) {
        DataBeanManager.grades=commonData.grade
        DataBeanManager.courses=commonData.subject
        if (DataBeanManager.grades.size>0){
            tv_grade.text=DataBeanManager.grades[grade-1].desc
        }
    }


    override fun layoutId(): Int {
        return R.layout.ac_cloud_storage
    }

    override fun initData() {
        mData= DataBeanManager.getIndexData()
        mData.removeFirst()
        mData.removeLast()
        mData[0].checked=true

        if (DataBeanManager.grades.size>0){
            tv_grade.text=DataBeanManager.grades[grade-1].desc
        }
        else{
            mCommonPresenter.getCommon()
        }

        tv_dynasty.text=DataBeanManager.popupDynasty()[0].name
    }

    override fun initView() {
        setPageTitle(R.string.cloud_storage_str)
        showView(tv_grade)

        bookcaseFragment = CloudBookCaseFragment()
        textbookFragment= CloudTextbookFragment()
        homeworkFragment = CloudHomeworkFragment()
        paperFragment = CloudExamFragment()
        noteFragment= CloudNoteFragment()
        paintingFragment = CloudPaintingFragment()

        switchFragment(lastFragment, bookcaseFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, mData).apply {
            rv_list.layoutManager = LinearLayoutManager(this@BookCollectActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                showGradeView()
                closeDynastyView()
                when (position) {
                    0 -> switchFragment(lastFragment, bookcaseFragment)//书架
                    1 -> switchFragment(lastFragment, textbookFragment)//课本
                    2 -> switchFragment(lastFragment, homeworkFragment)//作业
                    3 -> switchFragment(lastFragment, paperFragment)//考卷
                    4 -> switchFragment(lastFragment, noteFragment)//笔记
                    5 -> {
                        showDynastyView()
                        closeGradeView()
                        if (paintingFragment?.typeId==6||paintingFragment?.typeId==7){
                            closeDynastyView()
                        }
                        switchFragment(lastFragment, paintingFragment)//书画
                    }
                }
                lastPosition=position
            }
        }

        tv_grade.setOnClickListener {
            if (popWindowGrade==null)
            {
                popWindowGrade= PopupList(this,DataBeanManager.popupGrades,tv_grade,tv_grade.width,5).builder()
                popWindowGrade?.setOnSelectListener { item ->
                    tv_grade.text=item.name
                    grade=item.id
                    changeGrade(grade)
                }
            }
            else{
                popWindowGrade?.show()
            }
        }

        tv_dynasty.setOnClickListener {
            if (popWindowDynasty==null)
            {
                popWindowDynasty= PopupList(this,DataBeanManager.popupDynasty(),tv_dynasty,tv_dynasty.width,5).builder()
                popWindowDynasty?.setOnSelectListener { item ->
                    tv_dynasty.text=item.name
                    paintingFragment?.changeDynasty(item.id)
                }
            }
            else{
                popWindowDynasty?.show()
            }
        }

    }

    fun closeDynastyView(){
        disMissView(tv_dynasty)
    }

    fun closeGradeView(){
        disMissView(tv_grade)
    }

    fun showGradeView(){
        showView(tv_grade)
    }

    fun showDynastyView(){
        showView(tv_dynasty)
    }

    /**
     * 刷新各个fragment年级
     */
    private fun changeGrade(grade: Int){
        bookcaseFragment?.changeGrade(grade)
        textbookFragment?.changeGrade(grade)
        homeworkFragment?.changeGrade(grade)
        paperFragment?.changeGrade(grade)
        noteFragment?.changeGrade(grade)
        paintingFragment?.changeGrade(grade)
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }



}