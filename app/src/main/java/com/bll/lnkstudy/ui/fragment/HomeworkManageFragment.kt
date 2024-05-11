package com.bll.lnkstudy.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.HomeworkCommitDetailsDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.CourseItem
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import java.io.File

class HomeworkManageFragment: BaseMainFragment() {

    private var lastFragment: Fragment? = null
    private var mCoursePos=0
    private var currentCourses= mutableListOf<CourseItem>()
    private var fragments= mutableListOf<HomeworkFragment>()
    private var popWindowBeans = mutableListOf<PopupBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework_manage
    }

    override fun initView() {
        setTitle(R.string.main_homework_title)
        showView(iv_manager)

        popWindowBeans.add(PopupBean(0, getString(R.string.homework_commit_details_str) ))
        popWindowBeans.add(PopupBean(1, getString(R.string.homework_create_str) ))

        iv_manager.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            HomeworkCommitDetailsDialog(requireActivity(),1).builder()
                        }
                        1 -> {
                            if (currentCourses.size > 0) {
                                fragments[mCoursePos].addContentModule()
                            }
                        }
                    }
                }
        }

    }

    override fun lazyLoad() {
        initTab()
    }

    //设置头部索引
    private fun initTab() {
        if (currentCourses!=MethodManager.getCourses()){
            mCoursePos = 0
            itemTabTypes.clear()
            currentCourses=MethodManager.getCourses()
            for (i in currentCourses.indices) {
                itemTabTypes.add(ItemTypeBean().apply {
                    title=currentCourses[i].subject
                    isCheck=i==0
                })
            }
            mTabTypeAdapter?.setNewData(itemTabTypes)
            initFragment()
        }
    }

    override fun onTabClickListener(view: View, position: Int) {
        mCoursePos=position
        switchFragment(lastFragment, fragments[mCoursePos])
    }

    private fun initFragment(){
        removeAllFragment()
        fragments.clear()
        for (item in currentCourses){
            fragments.add(HomeworkFragment().newInstance(item))
        }
        if (fragments.size>0){
            switchFragment(lastFragment, fragments[mCoursePos])
        }
    }

    /**
     * 上传
     */
    fun upload(token: String) {
        if (grade == 0) return
        val cloudList = mutableListOf<CloudListBean>()
        //空内容不上传
        val nullItems = mutableListOf<HomeworkTypeBean>()
        val types= HomeworkTypeDaoManager.getInstance().queryAll()
        for (typeBean in types) {
            when (typeBean.state) {
                1 -> {
                    val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val homeworkContents = HomeworkPaperContentDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startUpload(path, typeBean.name)
                            setCallBack {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    contentJson = Gson().toJson(homePapers)
                                    contentSubtypeJson = Gson().toJson(homeworkContents)
                                    downloadUrl = it
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }
                    } else {
                        nullItems.add(typeBean)
                    }
                }
                2 -> {
                    val homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startUpload(path, typeBean.name)
                            setCallBack {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    contentJson = Gson().toJson(homeworks)
                                    downloadUrl = it
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }

                    } else {
                        nullItems.add(typeBean)
                    }
                }
                3 -> {
                    val records = RecordDaoManager.getInstance().queryAllByCourse(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathRecord(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startUpload(path, typeBean.name)
                            setCallBack {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    contentJson = Gson().toJson(records)
                                    downloadUrl = it
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }
                    } else {
                        nullItems.add(typeBean)
                    }
                }
                4 -> {
                    val homeworkBook = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
                    //判断题卷本是否已下载题卷书籍
                    if (homeworkBook == null) {
                        nullItems.add(typeBean)
                    } else {
                        //判读是否存在手写内容
                        if (FileUtils.isExistContent(homeworkBook.bookDrawPath)) {
                            FileUploadManager(token).apply {
                                startUpload(homeworkBook.bookPath, File(homeworkBook.bookPath).name)
                                setCallBack {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homeworkBook)
                                        downloadUrl = it
                                        zipUrl = homeworkBook.downloadUrl
                                        bookId = typeBean.bookId
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                            }
                        } else {
                            cloudList.add(CloudListBean().apply {
                                this.type = 2
                                subTypeStr = typeBean.course
                                date = typeBean.date
                                grade = typeBean.grade
                                listJson = Gson().toJson(typeBean)
                                contentJson = Gson().toJson(homeworkBook)
                                zipUrl = homeworkBook.downloadUrl
                                bookId = typeBean.bookId
                            })
                            startUpload(cloudList, nullItems)
                        }
                    }
                }
            }
        }

    }

    /**
     * 开始上传到云书库
     */
    private fun startUpload(list: MutableList<CloudListBean>, nullList: MutableList<HomeworkTypeBean>) {
        if (list.size == HomeworkTypeDaoManager.getInstance().queryAll().size - nullList.size)
            mCloudUploadPresenter.upload(list)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        setClearHomework()
        setSystemControlClear()
        for (fragment in fragments){
            fragment.clearData()
        }
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager
            val ft = fm?.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.add(R.id.fl_content_homework, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    private fun removeAllFragment(){
        val fm = activity?.supportFragmentManager!!
        val ft = fm.beginTransaction()
        for (fragment in fragments){
            ft.remove(fragment).commit()
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                lazyLoad()
            }
        }
    }

}