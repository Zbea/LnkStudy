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
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.iv_geometry
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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
            drawPaths.add("${pathStr}/${i+1}_draw.png")
        }

        startAlarmManager()
    }

    override fun initView() {
        disMissView(iv_catalog,iv_expand,iv_tool,iv_draft)
        setViewElikUnable(iv_geometry)

        if (grade>4){
            showView(iv_draft)
            if (AppDaoManager.getInstance().isTool(Constants.PACKAGE_GEOMETRY)){
                showView(iv_geometry)
            }
            else{
                disMissView(iv_geometry)
            }
        }

        onChangeExpandView()
        onChangeContent()

        iv_btn.setOnClickListener {
            if (!NetworkUtil(this).isNetworkConnected()){
                showToast(R.string.net_work_error)
                return@setOnClickListener
            }
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
        val date=Date(DateUtils.date10ToDate13(exam?.time!!))
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
            showToast("已过提交时间")
            return
        }
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.action = Constants.ACTION_EXAM_TIME
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.set(AlarmManager.RTC_WAKEUP,selectLong,pendingIntent)
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

        setElikLoadPath(page+1,elik_b!!,v_content_b!!)
        tv_page.text="${page+1+1}"
    }

    //加载图片
    private fun setElikLoadPath(index: Int, elik:EinkPWInterface, view: ImageView) {
        GlideUtils.setImageUrl(this,paths[index],view)
        elik.setLoadFilePath(drawPaths[index],true)
    }

    /**
     * 提交
     */
    private fun commit(){
        commitItems.clear()
        showLoading()
        for (i in paths.indices) {
            Thread{
                val path = paths[i] //当前原图路径
                val drawPath = drawPaths[i] //当前绘图路径
                drawPaths.add(drawPath)
                BitmapUtils.mergeBitmap(path,drawPath)
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

    override fun onDestroy() {
        super.onDestroy()
        alarmManager?.cancel(pendingIntent)
        alarmManager=null
        setExamMode(false)
    }

}