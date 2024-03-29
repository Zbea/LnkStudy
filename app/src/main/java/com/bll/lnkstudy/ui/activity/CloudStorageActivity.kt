package com.bll.lnkstudy.ui.activity

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.cloud.*
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_cloud_storage.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 云存储
 */
class CloudStorageActivity: BaseAppCompatActivity(){
    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var lastFragment: Fragment? = null

    private var bookcaseFragment: CloudBookCaseFragment? = null
    private var textbookFragment: CloudTextbookFragment? = null
    private var paperFragment: CloudExamFragment? = null
    private var homeworkFragment: CloudHomeworkFragment? = null
    private var noteFragment: CloudNoteFragment? = null
    private var paintingFragment: CloudPaintingFragment? = null

    var grade=mUser?.grade!!
    private var popWindowDynasty:PopupList?=null
    private var popWindowGrade:PopupList?=null

    override fun layoutId(): Int {
        return R.layout.ac_cloud_storage
    }

    override fun initData() {
        tv_grade.text=DataBeanManager.getGradeStr(grade)
        tv_course.text=DataBeanManager.popupDynasty()[0].name
    }

    override fun initView() {
        setPageTitle(R.string.cloud_storage_str)

        bookcaseFragment = CloudBookCaseFragment()
        textbookFragment= CloudTextbookFragment()
        homeworkFragment = CloudHomeworkFragment()
        paperFragment = CloudExamFragment()
        noteFragment= CloudNoteFragment()
        paintingFragment = CloudPaintingFragment()

        switchFragment(lastFragment, bookcaseFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataCloud()).apply {
            rv_list.layoutManager = LinearLayoutManager(this@CloudStorageActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                showGradeView()
                closeDynastyView()
                when (position) {
                    0 -> {
                        closeGradeView()
                        switchFragment(lastFragment, bookcaseFragment)//书架
                    }
                    1 -> switchFragment(lastFragment, textbookFragment)//课本
                    2 -> switchFragment(lastFragment, homeworkFragment)//作业
                    3 -> switchFragment(lastFragment, paperFragment)//考卷
                    4 -> switchFragment(lastFragment, noteFragment)//笔记
                    5 -> {
                        showDynastyView()
                        closeGradeView()
                        if (paintingFragment?.typeId==7||paintingFragment?.typeId==8){
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
                popWindowGrade= PopupList(this,DataBeanManager.popupGrades(grade),tv_grade,tv_grade.width,5).builder()
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

        tv_course.setOnClickListener {
            if (popWindowDynasty==null)
            {
                popWindowDynasty= PopupList(this,DataBeanManager.popupDynasty(),tv_course,tv_course.width,5).builder()
                popWindowDynasty?.setOnSelectListener { item ->
                    tv_course.text=item.name
                    paintingFragment?.changeDynasty(item.id)
                }
            }
            else{
                popWindowDynasty?.show()
            }
        }

    }

    fun closeDynastyView(){
        disMissView(tv_course)
    }

    private fun closeGradeView(){
        disMissView(tv_grade)
    }

    private fun showGradeView(){
        showView(tv_grade)
    }

    fun showDynastyView(){
        showView(tv_course)
    }

    /**
     * 刷新各个fragment年级
     */
    private fun changeGrade(grade: Int){
        paperFragment?.changeGrade(grade)
        textbookFragment?.changeGrade(grade)
        noteFragment?.changeGrade(grade)
        paintingFragment?.changeGrade(grade)
        homeworkFragment?.changeGrade(grade)
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

    override fun initChangeData() {
        bookcaseFragment?.changeInitData()
        textbookFragment?.changeInitData()
        homeworkFragment?.changeInitData()
        paperFragment?.changeInitData()
        noteFragment?.changeInitData()
        paintingFragment?.changeInitData()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
        closeNetwork()
    }

}