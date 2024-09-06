package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.EXAM_COMMIT_EVENT
import com.bll.lnkstudy.Constants.Companion.MESSAGE_COMMIT_EVENT
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.presenter.MainRightPresenter
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.MessageListActivity
import com.bll.lnkstudy.ui.activity.drawing.DiaryActivity
import com.bll.lnkstudy.ui.activity.drawing.ExamCommitDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_main_right.*
import org.greenrobot.eventbus.EventBus
import java.io.File


/**
 * 首页
 */
class MainRightFragment : BaseMainFragment(), IContractView.IMainRightView, IContractView.IMessageView{

    private val mMainPresenter = MainRightPresenter(this,2)
    private val mMessagePresenter=MessagePresenter(this,2)
    private var messages= mutableListOf<MessageList.MessageBean>()
    private var mMessageAdapter:MessageAdapter?=null
    private var privacyPassword=MethodManager.getPrivacyPassword(0)
    private var examItem: ExamItem?=null
    private var diaryStartLong=0L
    private var diaryEndLong=0L
    private var diaryUploadTitleStr=""

    override fun onList(message: MessageList) {
        if (message.list.isNotEmpty()){
            messages=message.list
            mMessageAdapter?.setNewData(messages)
        }
    }
    override fun onCommitSuccess() {
    }
    override fun onExam(exam: ExamItem) {
        examItem=exam
        loadPapers()
        initExamView()
    }

    override fun onCourse(url: String) {
        if (url!=SPUtil.getString("courseUrl")){
            SPUtil.putString("courseUrl",url)
            GlideUtils.setImageUrl(requireActivity(),url,iv_course)
        }
    }
    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        var currentGrade=0
        val oldGrade=mUser?.grade!!
        for (item in classGroups){
            if (item.state==1){
                currentGrade=item.grade
                break
            }
        }
        if (currentGrade!=oldGrade&&currentGrade>0){
            mUser?.grade=currentGrade
            SPUtil.putObj("user", mUser!!)
            EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
            //当年级变化时，及时上传本地作业、考卷
            EventBus.getDefault().post(Constants.USER_CHANGE_GRADE_EVENT)
        }
    }

    override fun onCourseItems(courses: MutableList<String>) {
        for (course in courses) {
            var path = ""
            val typeId = MethodManager.getExamTypeId(course)
            //创建作业错题本
            if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(typeId)) {
                val typeItem = HomeworkTypeBean()
                typeItem.name = "${course}错题本"
                typeItem.course = course
                typeItem.date = System.currentTimeMillis()
                typeItem.grade = mUser?.grade!!
                typeItem.typeId = typeId
                typeItem.createStatus = 0
                typeItem.state = 5
                HomeworkTypeDaoManager.getInstance().insertOrReplace(typeItem)
                path = FileAddress().getPathScreenHomework(typeItem.name, typeItem.grade)
            } else {
                path = FileAddress().getPathScreenHomework("${course}错题本", mUser?.grade!!)
            }
            FileUtils.mkdirs(path)

            //创建考卷分类
            if (PaperTypeDaoManager.getInstance().queryById(typeId)==null){
                val typeItem= PaperTypeBean()
                typeItem.name="学校考试卷"
                typeItem.course=course
                typeItem.date=System.currentTimeMillis()
                typeItem.grade=mUser?.grade!!
                typeItem.typeId=typeId
                PaperTypeDaoManager.getInstance().insertOrReplace(typeItem)
            }
        }
        if (courses.isNotEmpty() && courses != MethodManager.getCourses()) {
            MethodManager.saveCourses(courses)
            EventBus.getDefault().post(Constants.COURSEITEM_EVENT)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    override fun initView() {
        initMessageView()

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        tv_free_note.setOnClickListener {
            customStartActivity(Intent(activity,FreeNoteActivity::class.java))
        }

        tv_diray.setOnClickListener {
            if (privacyPassword!=null&&privacyPassword?.isSet==true){
                PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                    customStartActivity(Intent(activity,DiaryActivity::class.java))
                }
            }
            else{
                customStartActivity(Intent(activity,DiaryActivity::class.java))
            }
        }

        tv_diray.setOnLongClickListener {
            val pops= mutableListOf<PopupBean>()
            if (privacyPassword==null){
                pops.add(PopupBean(1,"设置密码"))
            }
            else{
                if (privacyPassword?.isSet==true){
                    pops.add(PopupBean(1,"取消密码"))
                }
                else{
                    pops.add(PopupBean(1,"设置密码"))
                }
            }
            pops.add(PopupBean(2,"上传日记"))
            pops.add(PopupBean(3,"删除日记"))
            PopupClick(requireActivity(),pops,tv_diray,5).builder().setOnSelectListener{
                when(it.id){
                    1->{
                        if (privacyPassword==null){
                            PrivacyPasswordCreateDialog(requireActivity()).builder().setOnDialogClickListener{
                                privacyPassword=it
                                showToast("日记密码设置成功")
                            }
                        }
                        else{
                            val titleStr=if (privacyPassword?.isSet==true) "确定取消密码？" else "确定设置密码？"
                            CommonDialog(requireActivity()).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                                        privacyPassword!!.isSet=!privacyPassword!!.isSet
                                        MethodManager.savePrivacyPassword(0,privacyPassword)
                                    }
                                }
                            })
                        }
                    }
                    2->{
                        DiaryManageDialog(requireActivity(),1).builder().setOnDialogClickListener{
                                titleStr,startLong,endLong->
                            diaryStartLong=startLong
                            diaryEndLong=endLong
                            diaryUploadTitleStr=titleStr
                            EventBus.getDefault().post(Constants.DIARY_UPLOAD_EVENT)
                        }
                    }
                    3->{
                        DiaryManageDialog(requireActivity(),2).builder().setOnDialogClickListener{
                                titleStr,startLong,endLong->
                            val diarys= DiaryDaoManager.getInstance().queryList(startLong, endLong)
                            for (item in diarys){
                                val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
                                FileUtils.deleteFile(File(path))
                                DiaryDaoManager.getInstance().delete(item)
                            }
                            showToast(2,"删除日记成功")
                        }
                    }
                }
            }
            return@setOnLongClickListener true
        }

    }

    override fun lazyLoad() {
        fetchCommonData()
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            findMessages()
            fetchExam()
            mMainPresenter.getTeacherCourse()
            mMainPresenter.getClassGroupList(false)
            mMainPresenter.getCourseItems()
        }
        val url=SPUtil.getString("courseUrl")
        GlideUtils.setImageUrl(requireActivity(),url,iv_course)
    }

    //消息相关处理
    private fun initMessageView() {
        mMessageAdapter=MessageAdapter(R.layout.item_main_message, null).apply {
            rv_list_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list_message.adapter = this
            bindToRecyclerView(rv_list_message)
        }
    }


    //作业相关
    private fun initExamView() {
        if (examItem?.examUrl.isNullOrEmpty()){
            disMissView(rl_exam)
            return
        }
        showView(rl_exam)
        examItem?.apply {
            tv_exam_course.text= subject
            tv_exam_title.text=name
            tv_exam_time.text=DateUtils.longToHour(time)+"之前提交"
            rl_exam.setOnClickListener {
                val pathStr = FileAddress().getPathTestPaper(commonTypeId, id)
                val files = FileUtils.getAscFiles(pathStr)
                if (files==null){
                    showLoading()
                    loadPapers()
                    return@setOnClickListener
                }
                if (DateUtils.date10ToDate13(time)<System.currentTimeMillis()){
                    showToast("已超时")
                    disMissView(rl_exam)
                    return@setOnClickListener
                }
                if (files.size == paths.size) {
                    val bundle = Bundle()
                    bundle.putSerializable("exam", this)
                    val intent = Intent(activity, ExamCommitDrawingActivity::class.java)
                    intent.putExtra("bundle", bundle)
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                    intent.putExtra("android.intent.extra.KEEP_FOCUS",true)
                    customStartActivity(intent)
                }
            }
        }
    }

    /**
     * 获取当前考试
     */
    private fun fetchExam() {
        mMainPresenter.getExam()
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=4
        map["type"]=2
        mMessagePresenter.getList(map,false)
    }

    //下载收到的图片
    private fun loadPapers() {
        if (examItem?.examUrl.isNullOrEmpty()){
            return
        }
        val pathStr = FileAddress().getPathTestPaper(examItem?.commonTypeId!!, examItem?.id!!)
        val files = FileUtils.getAscFiles(pathStr)
        val images=examItem?.examUrl!!.split(",").toMutableList()
        val paths= mutableListOf<String>()
        for (i in images.indices){
            paths.add("$pathStr/${i+1}.png")
        }
        examItem?.paths=paths
        if (files == null || files.size != images.size) {
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        hideLoading()
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        hideLoading()
                    }
                })
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            EXAM_COMMIT_EVENT -> {
                examItem=null
                disMissView(rl_exam)
            }
            MESSAGE_COMMIT_EVENT -> {
                findMessages()
            }
        }
    }

    override fun onNetworkConnectionSuccess() {
        lazyLoad()
    }

    override fun onRefreshData() {
        lazyLoad()
        if (examItem!=null){
            if (DateUtils.date10ToDate13(examItem?.time!!)<System.currentTimeMillis()){
                disMissView(rl_exam)
            }
        }
    }

    /**
     * 每年上传日记
     */
    fun uploadDiary(token: String,isAuto:Boolean) {
        val cloudList = mutableListOf<CloudListBean>()
        //每年自动上传
        if (isAuto){
            val oldYear=DateUtils.getYear()-1
            diaryUploadTitleStr="${oldYear}年日记自动上传"
            diaryStartLong=DateUtils.dateToStamp(oldYear,1,1)
            diaryEndLong=DateUtils.dateToStamp(oldYear,12,31)
        }
        val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        if (diarys.isNotEmpty()){
            val paths= mutableListOf<String>()
            for (item in diarys){
                paths.add(FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date)))
            }
            val time=System.currentTimeMillis()
            FileUploadManager(token).apply {
                startUpload(paths,DateUtils.longToString(time))
                setCallBack{
                    cloudList.add(CloudListBean().apply {
                        type=7
                        subTypeStr=diaryUploadTitleStr
                        year=DateUtils.getYear()
                        date=time
                        listJson= Gson().toJson(diarys)
                        downloadUrl=it
                    })
                    mCloudUploadPresenter.upload(cloudList)
                }
            }
        }

    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        for (item in diarys){
            val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
            FileUtils.deleteFile(File(path))
            DiaryDaoManager.getInstance().delete(item)
        }
    }

}