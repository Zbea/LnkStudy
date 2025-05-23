package com.bll.lnkstudy.ui.activity.drawing

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
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
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
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
    private var page = 0 //当前页码
    private var alarmManager:AlarmManager?=null
    private var pendingIntent:PendingIntent?=null

    override fun onToken(token: String) {
        //获取合图的图片，没有手写的页面那原图
        val commitPaths= mutableListOf<String>()
        for (i in paths.indices){
            val mergePath=getPathMergeStr(i)
            if (FileUtils.isExist(mergePath)){
                commitPaths.add(mergePath)
            }
            else{
                commitPaths.add(paths[i])
            }
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
        SPUtil.putBoolean(Constants.SP_EXAM_MODE,false)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        //设置考试模式
        setExamMode(true)
        SPUtil.putBoolean(Constants.SP_EXAM_MODE,true)
        isExpand=true
        screenPos=Constants.SCREEN_RIGHT
        exam=intent.getBundleExtra("bundle")?.getSerializable("exam") as ExamItem

        flags=exam?.type!!
        paths = exam?.paths!!
        pageCount = paths.size
        pathStr = FileAddress().getPathTestPaper(exam?.subject!!,exam!!.commonTypeId, exam!!.id)
        commonTypeId=exam?.commonTypeId!!

        for (i in paths.indices){
            drawPaths.add("${pathStr}/${i+1}_draw.png")
        }

        startAlarmManager()
    }

    override fun initView() {
        disMissView(iv_catalog,iv_expand,iv_tool)
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
            CommonDialog(this,screenPos).setContent("确定提交考卷？").builder().setDialogClickListener(
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
            return
        }
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.action = Constants.ACTION_EXAM_TIME
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.set(AlarmManager.SYS_RTC_WAKEUP,selectLong,pendingIntent)
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
        if (index<paths.size){
            GlideUtils.setImageUrl(this,paths[index],view)
            elik.setLoadFilePath(drawPaths[index],true)
        }
    }

    override fun onElikSava_a() {
        BitmapUtils.saveScreenShot(v_content_a, getPathMergeStr(page))
    }

    override fun onElikSava_b() {
        BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(page+1))
    }

    /**
     * 提交
     */
    private fun commit(){
        if (!NetworkUtil.isNetworkConnected()){
            showToast("网络连接失败，无法提交")
            return
        }
        showLoading()
        mUploadPresenter.getToken()
    }

    /**
     * 得到当前合图地址
     */
    private fun getPathMergeStr(index: Int):String{
        return pathStr+"/merge/${index+1}.png"//手绘地址
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