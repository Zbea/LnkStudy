package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.Constants.Companion.EXAM_COMMIT_EVENT
import com.bll.lnkstudy.Constants.Companion.MESSAGE_COMMIT_EVENT
import com.bll.lnkstudy.Constants.Companion.PASSWORD_EVENT
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.PrivacyPasswordDialog
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.CourseItem
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.presenter.MainRightPresenter
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.MessageListActivity
import com.bll.lnkstudy.ui.activity.drawing.DiaryActivity
import com.bll.lnkstudy.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkstudy.ui.activity.drawing.ExamCommitDrawingActivity
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.*
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
    private var privacyPassword=MethodManager.getPrivacyPassword()
    private var examItem: ExamItem?=null

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
    override fun onCourseItems(courseItems: MutableList<CourseItem>) {
        if (courseItems!=MethodManager.getCourses()){
            MethodManager.saveCourses(courseItems)
            EventBus.getDefault().post(Constants.COURSEITEM_EVENT)
        }
    }

    override fun onCourse(url: String) {
        SPUtil.putString("${mUser?.accountId}courseUrl",url)
        GlideUtils.setImageUrl(requireActivity(),url,iv_course)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    override fun initView() {
        initMessageView()
        initExamView()

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        tv_free_note.setOnClickListener {
            customStartActivity(Intent(activity,FreeNoteActivity::class.java))
        }

        tv_diarl.setOnClickListener {
            if (privacyPassword!=null&&privacyPassword?.isSet==true){
                PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                    customStartActivity(Intent(activity,DiaryActivity::class.java))
                }
            }
            else{
                customStartActivity(Intent(activity,DiaryActivity::class.java))
            }
        }

    }

    override fun lazyLoad() {
        fetchCommonData()
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            findMessages()
            fetchExam()
            mMainPresenter.getCourseItems()
            mMainPresenter.getTeacherCourse()
        }
        val url=SPUtil.getString("${mUser?.accountId}courseUrl")
        GlideUtils.setImageUrl(requireActivity(),url,iv_course)
    }

    //消息相关处理
    private fun initMessageView() {
        mMessageAdapter=MessageAdapter(0,R.layout.item_main_message, null).apply {
            rv_list_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list_message.adapter = this
            bindToRecyclerView(rv_list_message)
        }
    }


    //作业相关
    private fun initExamView() {
        if (examItem==null)
        {
            disMissView(rl_exam)
        }
        else{
            examItem?.apply {
                rl_exam.visibility=if (exam.id==0) View.GONE else View.VISIBLE
                tv_exam_course.text= exam.subject
                tv_exam_type.text=exam.examName
                tv_exam_title.text=exam.title
                tv_exam_time.text=DateUtils.longToHour(exam.endTime)+"之前提交"
                rl_exam.setOnClickListener {
                    val pathStr = FileAddress().getPathTestPaper(exam.commonTypeId, exam.id)
                    val files = FileUtils.getFiles(pathStr)
                    if (files==null){
                        showLoading()
                        loadPapers()
                        return@setOnClickListener
                    }
                    if (DateUtils.date10ToDate13(exam.endTime)<System.currentTimeMillis()){
                        showToast(2,"已超时")
                        return@setOnClickListener
                    }
                    if (files.size == exam.paths.size) {
                        val bundle = Bundle()
                        bundle.putSerializable("exam", exam)
                        val intent = Intent(activity, ExamCommitDrawingActivity::class.java)
                        intent.flags = type
                        intent.putExtra("bundle", bundle)
                        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                        intent.putExtra("android.intent.extra.KEEP_FOCUS",true)
                        customStartActivity(intent)
                    }
                }
            }
        }
    }

    /**
     * 获取当前考试
     */
    private fun fetchExam() {
        val map= HashMap<String, Any>()
        map["type"] = 2
        mMainPresenter.getExam(map)
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
        if (examItem==null||examItem?.exam?.id==0)
            return
        examItem?.apply {
            //设置路径
            val pathStr = FileAddress().getPathTestPaper(exam.commonTypeId, exam.id)
            val files = FileUtils.getFiles(pathStr)
            val images=exam.imageUrl.split(",").toMutableList()
            val paths= mutableListOf<String>()
            for (i in images.indices){
                paths.add("$pathStr/${i+1}.png")
            }
            exam.paths=paths
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
    }

    /**
     * 半年删除日记
     */
    fun deleteDiary(){
        val time=System.currentTimeMillis()-Constants.halfYear
        val diarys=DiaryDaoManager.getInstance().queryList(time)
        for (item in diarys){
            DiaryDaoManager.getInstance().delete(item)
            FileUtils.deleteFile(File(FileAddress().getPathDiary(DateUtils.longToString(item.date))))
            val id=item.date.div(1000).toInt()
            DataUpdateManager.deleteDateUpdate(9,id,1,id)
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
            PASSWORD_EVENT->{
                privacyPassword=MethodManager.getPrivacyPassword()
            }
        }
    }

    override fun onNetworkConnectionSuccess() {
        lazyLoad()
    }

    override fun onRefreshData() {
        lazyLoad()
        if (examItem!=null){
            if (DateUtils.date10ToDate13(examItem?.exam?.endTime!!)<System.currentTimeMillis()){
                disMissView(rl_exam)
            }
        }
    }

}