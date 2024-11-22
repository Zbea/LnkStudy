package com.bll.lnkstudy.ui.activity

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.cloud.CloudBookCaseFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudDiaryFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudExamFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudHomeworkFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudNoteFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudPaintingManagerFragment
import com.bll.lnkstudy.ui.fragment.cloud.CloudTextbookFragment
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_cloud_storage.rv_list
import kotlinx.android.synthetic.main.common_title.tv_grade

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
    private var paintingFragment: CloudPaintingManagerFragment? = null
    private var diaryFragment: CloudDiaryFragment? = null

    private var popWindowGrade:PopupList?=null

    override fun layoutId(): Int {
        return R.layout.ac_cloud_storage
    }

    override fun initData() {
        tv_grade.text=DataBeanManager.getGradeStr(grade)
    }

    override fun initView() {
        setPageTitle(R.string.cloud_storage_str)

        bookcaseFragment = CloudBookCaseFragment()
        textbookFragment= CloudTextbookFragment()
        homeworkFragment = CloudHomeworkFragment()
        paperFragment = CloudExamFragment()
        noteFragment= CloudNoteFragment()
        paintingFragment = CloudPaintingManagerFragment()
        diaryFragment= CloudDiaryFragment()

        switchFragment(lastFragment, bookcaseFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataCloud()).apply {
            rv_list.layoutManager = LinearLayoutManager(this@CloudStorageActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                showGradeView()
                when (position) {
                    0 -> {
                        closeGradeView()
                        switchFragment(lastFragment, bookcaseFragment)//书架
                    }
                    1 -> switchFragment(lastFragment, textbookFragment)//课本
                    2 -> switchFragment(lastFragment, homeworkFragment)//作业
                    3 -> switchFragment(lastFragment, paperFragment)//考卷
                    4 -> {
                        closeGradeView()
                        switchFragment(lastFragment, noteFragment)//笔记
                    }
                    5 -> {
                        closeGradeView()
                        switchFragment(lastFragment, paintingFragment)//书画
                    }
                    6->{
                        closeGradeView()
                        switchFragment(lastFragment, diaryFragment)//书画
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

    }

    private fun closeGradeView(){
        disMissView(tv_grade)
    }

    private fun showGradeView(){
        showView(tv_grade)
    }

    /**
     * 刷新各个fragment年级
     */
    private fun changeGrade(grade: Int){
        paperFragment?.changeGrade(grade)
        textbookFragment?.changeGrade(grade)
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

    override fun initChangeScreenData() {
        bookcaseFragment?.initChangeScreenData()
        textbookFragment?.initChangeScreenData()
        homeworkFragment?.initChangeScreenData()
        paperFragment?.initChangeScreenData()
        noteFragment?.initChangeScreenData()
        paintingFragment?.initChangeScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}