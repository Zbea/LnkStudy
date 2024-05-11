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
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkstudy.dialog.PrivacyPasswordDialog
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.presenter.MainRightPresenter
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.MessageListActivity
import com.bll.lnkstudy.ui.activity.drawing.DiaryActivity
import com.bll.lnkstudy.ui.activity.drawing.ExamCommitDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkstudy.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.*
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_main_right.*


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
        SPUtil.putString("courseUrl",url)
        GlideUtils.setImageUrl(requireActivity(),url,iv_course)
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

        tv_plan.setOnClickListener {
            customStartActivity(Intent(activity, PlanOverviewActivity::class.java))
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
            return@setOnLongClickListener true
        }

    }

    override fun lazyLoad() {
        fetchCommonData()
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            findMessages()
            fetchExam()
            mMainPresenter.getTeacherCourse()
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
                val files = FileUtils.getFiles(pathStr)
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
        val pathStr = FileAddress().getPathTestPaper(examItem?.commonTypeId!!, id)
        val files = FileUtils.getFiles(pathStr)
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

}