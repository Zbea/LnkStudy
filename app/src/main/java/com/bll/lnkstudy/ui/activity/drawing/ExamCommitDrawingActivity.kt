package com.bll.lnkstudy.ui.activity.drawing

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.EinkPWInterface
import android.view.KeyEvent
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MyBroadcastReceiver
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

/**
 * 考卷考试页面
 */
class ExamCommitDrawingActivity : BaseDrawingActivity(),IContractView.IFileUploadView{
    private var flags=0
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private var commonTypeId=0
    private var pathStr=""
    private var exam: ExamItem?=null
    private var paths = mutableListOf<String>()
    private var drawPaths = mutableListOf<String>()
    private val commitItems = mutableListOf<ItemList>()

    private var page = 0 //当前页码
    private var alarmManager:AlarmManager?=null
    private var pendingIntent:PendingIntent?=null

    override fun onToken(token: String) {
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    //老师测试卷
                    if (flags==1){
                        val map= HashMap<String, Any>()
                        map["studentTaskId"]=exam?.id!!
                        map["commonTypeId"]=exam?.commonTypeId!!
                        map["studentUrl"]=ToolUtils.getImagesStr(urls)
                        mUploadPresenter.commit(map)
                    }
                    else{//年级考卷
                        val map= HashMap<String, Any>()
                        map["studentTaskId"]=exam?.id!!
                        map["studentUrl"]=ToolUtils.getImagesStr(urls)
                        mUploadPresenter.commitExam(map)
                    }
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        //删除本地考试卷以及手写
        FileUtils.deleteFile(File(pathStr))
        EventBus.getDefault().post(Constants.EXAM_COMMIT_EVENT)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        setExamMode(true)
        screenPos=Constants.SCREEN_RIGHT
        isExpand=true
        exam=intent.getBundleExtra("bundle")?.getSerializable("exam") as ExamItem
        flags=exam?.type!!
        paths = exam?.paths!!
        pageCount = paths.size
        pathStr = FileAddress().getPathTestPaper(exam!!.commonTypeId, exam!!.id)
        commonTypeId=exam?.commonTypeId!!

        for (i in paths.indices){
            drawPaths.add("${pathStr}/${i+1}/draw.tch")
        }

        startAlarmManager()
    }

    override fun initView() {
        disMissView(iv_catalog,iv_draft,iv_expand,iv_tool)
        showView(iv_geometry)
        setViewElikUnable(iv_geometry)

        if (AppDaoManager.getInstance().isTool(Constants.PACKAGE_GEOMETRY)){
            showView(iv_geometry)
        }
        else{
            disMissView(iv_geometry)
        }

        onChangeExpandView()
        onChangeContent()

        iv_btn.setOnClickListener {
            CommonDialog(this,screenPos).setContent(R.string.toast_commit_ok).builder().setDialogClickListener(
                object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        commit()
                    }
                })
        }
    }

    /**
     * 开启定时任务
     */
    private fun startAlarmManager(){
        val date=Date(exam?.time!!*1000)
        val mCalendar = Calendar.getInstance()
        val currentTimeMillisLong = System.currentTimeMillis()
        mCalendar.timeInMillis = currentTimeMillisLong
        mCalendar.timeZone = TimeZone.getTimeZone("GMT+8")
        mCalendar.set(Calendar.HOUR_OF_DAY, date.hours)
        mCalendar.set(Calendar.MINUTE, date.minutes)
        mCalendar.set(Calendar.SECOND, 0)
        mCalendar.set(Calendar.MILLISECOND, 0)

        val selectLong = mCalendar.timeInMillis

        if (currentTimeMillisLong > selectLong) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1)
//            selectLong = mCalendar.timeInMillis
            showToast("已过提交时间")
            return
        }
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.action = Constants.ACTION_EXAM_TIME
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP, selectLong,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }


    override fun onPageDown() {
        if (page<pageCount-1){
            page+=2
        }
        onChangeContent()
    }

    override fun onPageUp() {
        if (page>0){
            page-=2
        }
        onChangeContent()
    }

    private fun onChangeContent(){
        if (page>pageCount-2&&pageCount>1)
            page=pageCount-2
        if (page<0)
            page=0

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        setElikLoadPath(page,elik_a!!,v_content_a!!)
        tv_page_a.text="${page+1}"

        if (page+1<pageCount){
            elik_b?.setPWEnabled(true)
            setElikLoadPath(page+1,elik_b!!,v_content_b!!)
            tv_page.text="${page+1+1}"
        }
        else{
            elik_b?.setPWEnabled(false)
            v_content_b?.setImageResource(0)
            tv_page.text=""
        }
    }

    //加载图片
    private fun setElikLoadPath(index: Int, elik:EinkPWInterface, view: ImageView) {
        GlideUtils.setImageNoCacheUrl(this,paths[index],view)
        elik.setLoadFilePath(drawPaths[index],true)
    }

    override fun onElikSava_a() {
        elik_a?.saveBitmap(true) {}
    }
    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    /**
     * 提交
     */
    private fun commit(){
        commitItems.clear()
        if (NetworkUtil(this@ExamCommitDrawingActivity).isNetworkConnected()){
            showLoading()
            for (i in paths.indices) {
                Thread{
                    val path = paths[i] //当前原图路径
                    val drawPath = drawPaths[i].replace("tch","png") //当前绘图路径
                    BitmapUtils.mergeBitmap(path,drawPath)
                    //删除手写
                    FileUtils.deleteFile(File(drawPath).parentFile)
                    commitItems.add(ItemList().apply {
                        id = i
                        url = path
                    })
                    if (commitItems.size==paths.size){
                        commitItems.sort()
                        mUploadPresenter.getToken()
                    }
                }.start()
            }
        }
        else{
            showNetworkDialog()
        }
    }


    /**
     * 设置考试模式
     */
    private fun setExamMode(isMode:Boolean){
        val intent = Intent()
        intent.putExtra("exam", if (isMode)1 else 0)
        intent.action = Constants.EXAM_MODE_BROADCAST_EVENT
        sendBroadcast(intent)
    }

    override fun onBackPressed() {
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.EXAM_TIME_EVENT){
            commit()
        }
    }

    override fun onNetworkConnectionSuccess() {
        commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmManager?.cancel(pendingIntent)
        alarmManager=null
        closeNetwork()
        setExamMode(false)
    }

}