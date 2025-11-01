package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkShareDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.homework.HomeworkUnfinishedMessageAllActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.iv_tips
import kotlinx.android.synthetic.main.common_fragment_title.rl_message
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn_1
import java.io.File

class HomeworkManageFragment: BaseMainFragment(), IHomeworkView {
    private val mPresenter = HomeworkPresenter(this)
    private var lastFragment: Fragment? = null
    private var mCoursePos=0
    private var fragments= mutableListOf<HomeworkFragment>()
    private val otherCourse= mutableListOf("美术","音乐","科学","道法","信息","体育")

    override fun onMessageAll(list: MutableList<HomeworkMessageList.MessageBean>) {
        DataBeanManager.homeworkMessages=list
        if (list.isNotEmpty()){
            showView(iv_tips)
        }
        else{
            disMissView(iv_tips)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework_manage
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[3])
        showView(rl_message,tv_btn_1)

        rl_message.setOnClickListener {
            customStartActivity(Intent(requireActivity(), HomeworkUnfinishedMessageAllActivity::class.java))
        }

        tv_btn_1.text="创建作业本"
        tv_btn_1.setOnClickListener {
            if (itemTabTypes.isNotEmpty() && grade >0) {
                fragments[mCoursePos].addContentModule()
            }
        }

        fetchGrade()
    }

    override fun lazyLoad() {
        if (fragments.isEmpty()){
            initTab()
        }
        else{
            for (fragment in fragments){
                fragment.onRefreshData()
            }
        }

        clearRepeatHomeworkType()
        fetchData()
    }

    //设置头部索引
    private fun initTab() {
        mCoursePos = 0
        lastFragment=null
        setTabCourse()
        setLocalHomeworkType()
        initFragment()
    }

    override fun onTabClickListener(view: View, position: Int) {
        mCoursePos=position
        switchFragment(lastFragment, fragments[mCoursePos])
    }


    private fun initFragment(){
        removeAllFragment()
        fragments.clear()
        //将所有科目全部添加
        val ft=getFragmentTransaction()
        for (courseItem in itemTabTypes){
            val fragment=HomeworkFragment.newInstance(courseItem.title)
            ft.add(R.id.fl_content_homework, fragment).hide(fragment)
            fragments.add(fragment)
        }
        ft.commit()

        if (fragments.size>0){
            switchFragment(lastFragment, fragments[mCoursePos])
        }
    }

    /**
     * 清除重复的本地默认生成作业本（又grade=0引起的typeId不同）
     */
    private fun clearRepeatHomeworkType(){
        val items=HomeworkTypeDaoManager.getInstance().queryAllByCreate(3)
        for (item in items){
            if (item.typeId%10==0)
               HomeworkTypeDaoManager.getInstance().deleteBean(item)
        }
    }

    /**
     * 设置本地错题本
     */
    private fun setLocalHomeworkType(){
        if (grade==0){
            return
        }
        for (item in itemTabTypes){
            val course=item.title
            //删除错题本
            HomeworkTypeDaoManager.getInstance().deleteBean(MethodManager.getHomeworkTypeId(course,5))
            val path = FileAddress().getPathScreenHomework("${course}错题本", grade)
            FileUtils.delete(path)

            val name="${course}分享本"
            val typeId=MethodManager.getHomeworkTypeId(course,9)
            val localType=HomeworkTypeDaoManager.getInstance().queryByTypeId(typeId)
            if (localType==null) {
                val typeItem = HomeworkTypeBean()
                typeItem.name = name
                typeItem.course = course
                typeItem.date = System.currentTimeMillis()
                typeItem.grade = grade
                typeItem.state = 9
                typeItem.typeId = typeId
                typeItem.createStatus=3
                typeItem.fromStatus=3
                HomeworkTypeDaoManager.getInstance().insertOrReplace(typeItem)
            }
        }
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment) {
        if (from != to) {
            lastFragment = to
            val ft = getFragmentTransaction()
            if (from != null) {
                ft.hide(from)
            }
            ft.show(to).commit()
        }
    }

    private fun removeAllFragment(){
        for (fragment in fragments){
            getFragmentTransaction().remove(fragment).commit()
        }
    }

    private fun getFragmentTransaction():FragmentTransaction{
        val fm = activity?.supportFragmentManager!!
        return fm.beginTransaction()
    }

    /**
     * 上传
     */
    fun upload(token: String) {
        val cloudList = mutableListOf<CloudListBean>()
        //空内容不上传
        val nullItems = mutableListOf<HomeworkTypeBean>()
        val types= HomeworkTypeDaoManager.getInstance().queryAllExceptCloud(grade)
        for (typeBean in types) {
            when (typeBean.state) {
                1,7 -> {
                    val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homePapers)
                                        downloadUrl = url
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(path, typeBean.name)
                        }
                    } else {
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    }
                }
                3 -> {
                    val records = RecordDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(records)
                                        downloadUrl = url
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(path, typeBean.name)
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
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homeworkBook)
                                        contentSubtypeJson=Gson().toJson(homeworkBookCorrects)
                                        downloadUrl = url
                                        zipUrl=homeworkBook.downloadUrl
                                        bookId = typeBean.bookId
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(homeworkBook.bookDrawPath, homeworkBook.bookId.toString()+"draw")
                        }
                    }
                }
                5->{
                    val path=FileAddress().getPathScreenHomework(typeBean.name,typeBean.grade)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        downloadUrl = url
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(path, typeBean.name+typeBean.grade)
                        }
                    }
                    else{
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    }
                }
                9->{
                    val shareBeans = HomeworkShareDaoManager.getInstance().queryAll(typeBean.typeId)
                    val path=FileAddress().getPathHomework(typeBean.course,typeBean.typeId)
                    if (shareBeans.isNullOrEmpty()&&FileUtils.isExistContent(path)){
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(shareBeans)
                                        downloadUrl = url
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(path, typeBean.name)
                        }
                    } else {
                        nullItems.add(typeBean)
                        startUpload(cloudList, nullItems)
                    }
                }
                else -> {
                    val homeworks = HomeworkContentDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                    val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                    if (FileUtils.isExistContent(path)) {
                        FileUploadManager(token).apply {
                            setCallBack(object : FileUploadManager.UploadCallBack {
                                override fun onUploadSuccess(url: String) {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homeworks)
                                        downloadUrl = url
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                                override fun onUploadFail() {
                                }
                            })
                            startZipUpload(path, typeBean.name)
                        }
                    } else {
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
        if (cloudList.size == HomeworkTypeDaoManager.getInstance().queryAllExceptCloud(grade).size - nullList.size){
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

    /**
     * 重置作业本 （清除以及重新加载）
     */
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
        //分享本
        HomeworkShareDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.HOMEWORK_PATH))
        FileUtils.deleteHomework(File(FileAddress().getPathScreen("未分类")).parent)

        DataUpdateManager.clearDataUpdate(2)

        val map=HashMap<String,Any>()
        map["type"]=2
        mDataUploadPresenter.onDeleteData(map)
    }

    override fun fetchData() {
        if(NetworkUtil.isNetworkConnected()){
            mPresenter.getMessageAll(false)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                initTab()
            }
            Constants.CLEAR_HOMEWORK_EVENT->{
                clearHomeworkType()
            }
            Constants.HOMEWORK_MESSAGE_TIPS_EVENT->{
                if (DataBeanManager.homeworkMessages.isNotEmpty()){
                    showView(iv_tips)
                }
                else{
                    disMissView(iv_tips)
                }
            }
            Constants.MQTT_HOMEWORK_NOTICE_EVENT->{
                fetchData()
            }
        }
    }

    override fun onRefreshData() {
        fetchGrade()
        lazyLoad()
    }

    override fun onNetworkConnectionSuccess() {
        lazyLoad()
    }

}