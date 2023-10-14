package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.Constants.Companion.COURSE_EVENT
import com.bll.lnkstudy.Constants.Companion.EXAM_COMMIT_EVENT
import com.bll.lnkstudy.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkstudy.Constants.Companion.PASSWORD_EVENT
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CourseModuleDialog
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.dialog.PrivacyPasswordDialog
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.presenter.MainPresenter
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.*
import com.bll.lnkstudy.ui.activity.drawing.DiaryActivity
import com.bll.lnkstudy.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkstudy.ui.activity.drawing.PaperExamDrawingActivity
import com.bll.lnkstudy.ui.adapter.*
import com.bll.lnkstudy.utils.*
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_main_right.*
import java.io.File
import java.util.*


/**
 * 首页
 */
class MainRightFragment : BaseFragment(), IContractView.IMainView, IContractView.IMessageView{

    private val mMainPresenter = MainPresenter(this)
    private val mMessagePresenter=MessagePresenter(this)
    private var examPapers = mutableListOf<PaperList.PaperListBean>()
    private var positionPaper = 0
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MessageAdapter?=null
    private var privacyPassword=SPUtil.getObj("${mUser?.accountId}notePassword",
        PrivacyPassword::class.java)

    override fun onList(message: Message) {
        if (message.list.isNotEmpty()){
            messages=message.list
            mMessageAdapter?.setNewData(messages)
        }
    }
    override fun onCommitSuccess() {
    }

    override fun onExam(exam: PaperList?) {
        examPapers= exam?.list as MutableList<PaperList.PaperListBean>
        loadPapers()
        initExamView()
    }
    override fun onHomeworkNotice(list: HomeworkNoticeList?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    override fun initView() {

        initMessageView()
        initExamView()
        initCourse()

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        tv_class_template.setOnClickListener {
            CourseModuleDialog(requireActivity(), screenPos).builder()
                ?.setOnClickListener { type ->
                    customStartActivity(
                        Intent(activity, MainCourseActivity::class.java)
                            .setFlags(0)
                            .putExtra("courseType", type)
                    )
                }
        }

        ll_course.setOnClickListener {
            val courseType = SPUtil.getInt("courseType")
            customStartActivity(
                Intent(activity, MainCourseActivity::class.java).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .putExtra("courseType", courseType)
            )
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
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            findMessages()
            fetchExam()
        }
    }

    //课程表相关处理
    @SuppressLint("WrongConstant")
    private fun initCourse() {
        val path=Constants.IMAGE_PATH + "/course.png"
        if (File(path).exists())
            GlideUtils.setImageNoCacheUrl(activity,path , iv_course)
    }


    //消息相关处理
    private fun initMessageView() {
        mMessageAdapter=MessageAdapter(0,R.layout.item_main_message, null).apply {
            rv_list_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list_message.adapter = this
            bindToRecyclerView(rv_list_message)
            setOnItemClickListener { adapter, view, position ->
                MessageDetailsDialog(requireContext(), screenPos, messages[position]).builder()
            }
        }
    }


    //作业相关
    private fun initExamView() {
        rl_exam.visibility=if (examPapers.size>0) View.VISIBLE else View.GONE
        if (examPapers.size>0){
            tv_exam_course.text= examPapers[0].subject
            tv_exam_type.text=examPapers[0].examName
            tv_exam_title.text=examPapers[0].title
            tv_exam_time.text=DateUtils.longToHour(examPapers[0].endTime)+"之前提交"
        }
        rl_exam.setOnClickListener {
            val paper = examPapers[positionPaper]
            val files = FileUtils.getFiles(paper.path)
            if (files==null){
                showLoading()
                loadPapers()
                return@setOnClickListener
            }
            if (files.size == paper.paths.size) {
                val bundle = Bundle()
                bundle.putSerializable("exam", paper)
                val intent = Intent(activity, PaperExamDrawingActivity::class.java)
                intent.putExtra("bundle", bundle)
                intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                intent.putExtra("android.intent.extra.KEEP_FOCUS",true)
                customStartActivity(intent)
            }
        }
    }

    /**
     * 获取当前考试
     */
    private fun fetchExam() {
        val map= HashMap<String, Any>()
        map["size"] = 100
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
        for (item in examPapers) {
            //设置路径
            val pathStr = FileAddress().getPathTestPaper(item.examId, item.id)
            val files = FileUtils.getFiles(pathStr)
            val images=item.imageUrl.split(",").toMutableList()
            val paths= mutableListOf<String>()
            for (i in images.indices){
                paths.add("$pathStr/${i+1}.png")
            }
            item.path = pathStr
            item.paths=paths
            if (files == null || files.size > images.size) {
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
            COURSE_EVENT -> {
                initCourse()
            }
            EXAM_COMMIT_EVENT -> {
                examPapers.removeAt(0)
                disMissView(rl_exam)
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
            PASSWORD_EVENT->{
                privacyPassword=SPUtil.getObj("${mUser?.accountId}notePassword",
                    PrivacyPassword::class.java)
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}