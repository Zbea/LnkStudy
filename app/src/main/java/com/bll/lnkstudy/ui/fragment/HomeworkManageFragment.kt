package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.HomeworkCommitDetailsDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.CourseManagerActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import java.io.File

class HomeworkManageFragment: BaseMainFragment(), IHomeworkView {
    private val mPresenter = HomeworkPresenter(this)
    private var lastFragment: Fragment? = null
    private var mCoursePos=0
    private var currentCourses= mutableListOf<ItemTypeBean>()
    private var fragments= mutableListOf<HomeworkFragment>()
    private var popWindowBeans = mutableListOf<PopupBean>()
    private val otherCourse= mutableListOf("美术","音乐","科学","道法","信息","体育")

    override fun onCommitDetails(list: HomeworkCommitMessageList) {
        HomeworkCommitDetailsDialog(requireActivity(),list.list).builder()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework_manage
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[3])
        showView(iv_manager)

        popWindowBeans.add(PopupBean(0, getString(R.string.homework_commit_details_str) ))
        popWindowBeans.add(PopupBean(1,"科目排序"))
        popWindowBeans.add(PopupBean(2, getString(R.string.homework_create_str) ))

        iv_manager.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            mPresenter.onCommitMessage()
                        }
                        1->{
                            customStartActivity(Intent(requireActivity(),CourseManagerActivity::class.java))
                        }
                        2 -> {
                            if (currentCourses.isNotEmpty() && mUser?.grade!! >0) {
                                fragments[mCoursePos].addContentModule()
                            }
                        }
                    }
                }
        }

        initTab()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {
        val courseItems=ItemTypeDaoManager.getInstance().queryAll(7)
        if (currentCourses!=courseItems){
            mCoursePos = 0
            itemTabTypes.clear()
            currentCourses=courseItems
            for (i in currentCourses.indices) {
                itemTabTypes.add(ItemTypeBean().apply {
                    title=currentCourses[i].title
                    isCheck=i==0
                })
            }
            mTabTypeAdapter?.setNewData(itemTabTypes)
            setLocalHomeworkType()
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
        for (courseItem in currentCourses){
            fragments.add(HomeworkFragment().newInstance(courseItem.title))
        }
        if (fragments.size>0){
            switchFragment(lastFragment, fragments[mCoursePos])
        }
    }

    /**
     * 设置本地错题本
     */
    private fun setLocalHomeworkType(){
        for (item in currentCourses){
            val course=item.title
            //添加错题本
            if (!otherCourse.contains(course)){
                var path = ""
                val name="${course}错题本"
                val localType=HomeworkTypeDaoManager.getInstance().queryByNameGrade(name,grade)
                //创建作业错题本
                if (localType==null) {
                    val typeItem = HomeworkTypeBean()
                    typeItem.name = name
                    typeItem.course = course
                    typeItem.date = System.currentTimeMillis()
                    typeItem.grade = grade
                    typeItem.typeId = ToolUtils.getDateId()
                    typeItem.state = 5
                    typeItem.createStatus=3
                    typeItem.fromStatus=3
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(typeItem)
                    path = FileAddress().getPathScreenHomework(typeItem.name, typeItem.grade)
                } else {
                    path = FileAddress().getPathScreenHomework(name, grade)
                }
                if (!FileUtils.isExist(path))
                    FileUtils.mkdirs(path)
            }
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
        for (fragment in fragments){
            val fm = activity?.supportFragmentManager!!
            val ft = fm.beginTransaction()
            ft.remove(fragment).commit()
        }
    }

    /**
     * 上传
     */
    fun upload(token: String) {
        val cloudList = mutableListOf<CloudListBean>()
        //空内容不上传
        val nullItems = mutableListOf<HomeworkTypeBean>()
        val types= HomeworkTypeDaoManager.getInstance().queryAllExceptCloud()
        for (typeBean in types) {
            when (typeBean.state) {
                1 -> {
                    val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startZipUpload(path, typeBean.name)
                            setCallBack {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    contentJson = Gson().toJson(homePapers)
                                    downloadUrl = it
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }
                    } else {
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    }
                }
                2,6 -> {
                    val homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startZipUpload(path, typeBean.name)
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
                        startUpload(cloudList, nullItems)
                    }
                }
                3 -> {
                    val records = RecordDaoManager.getInstance().queryAllByCourse(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startZipUpload(path, typeBean.name)
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
                        startUpload(cloudList, nullItems)
                    }
                }
                4 -> {
                    val homeworkBook = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
                    val homeworkBookCorrects=HomeworkBookCorrectDaoManager.getInstance().queryCorrectAll(typeBean.bookId)
                    //判断题卷本是否已下载题卷书籍
                    if (homeworkBook == null) {
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    } else {
                        //判读是否存在手写内容
                        if (FileUtils.isExistContent(homeworkBook.bookPath)) {
                            FileUploadManager(token).apply {
                                startZipUpload(homeworkBook.bookPath, File(homeworkBook.bookPath).name)
                                setCallBack {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homeworkBook)
                                        contentSubtypeJson=Gson().toJson(homeworkBookCorrects)
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
                                contentSubtypeJson=Gson().toJson(homeworkBookCorrects)
                                downloadUrl = homeworkBook.downloadUrl
                                bookId = typeBean.bookId
                            })
                            startUpload(cloudList, nullItems)
                        }
                    }
                }
                5->{
                    val path=FileAddress().getPathScreenHomework(typeBean.name,typeBean.grade)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            startZipUpload(path, typeBean.name+typeBean.grade)
                            setCallBack {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    downloadUrl = it
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }
                    }
                    else{
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    }
                }
            }
        }

    }

    /**
     * 开始上传到云书库
     */
    private fun startUpload(cloudList: MutableList<CloudListBean>, nullList: MutableList<HomeworkTypeBean>) {
        if (cloudList.size == HomeworkTypeDaoManager.getInstance().queryAllExceptCloud().size - nullList.size){
            if (cloudList.size>0){
                mCloudUploadPresenter.upload(cloudList)
            }
            else{
                clearHomeworkType()
            }
        }

    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        clearHomeworkType()
    }

    private fun clearHomeworkType(){
        setClearHomework()
        setLocalHomeworkType()
        onRefreshData()
    }

    /**
     * 清空作业本
     */
    private fun setClearHomework(){
        //删除所有作业分类
        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有朗读
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()
        //题卷本批改详情
        HomeworkBookCorrectDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.HOMEWORK_PATH))
        FileUtils.deleteHomework(File(FileAddress().getPathScreen("未分类")).parent)
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(2)
        val map=HashMap<String,Any>()
        map["type"]=2
        mDataUploadPresenter.onDeleteData(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                initTab()
            }
        }
    }

    override fun onRefreshData() {
        for (fragment in fragments){
            fragment.onRefreshData()
        }
    }

}