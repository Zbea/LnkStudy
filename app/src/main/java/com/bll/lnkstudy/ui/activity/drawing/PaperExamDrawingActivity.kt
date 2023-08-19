package com.bll.lnkstudy.ui.activity.drawing

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.AlarmService
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.EventBusData
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

/**
 * 考卷考试页面
 */
class PaperExamDrawingActivity : BaseDrawingActivity(),IContractView.IFileUploadView{

    private val mUploadPresenter=FileUploadPresenter(this)
    private var course=""
    private var commonTypeId=0
    private var daoManager=PaperDaoManager.getInstance()
    private var daoContentManager=PaperContentDaoManager.getInstance()
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()

    private var exam: PaperList.PaperListBean?=null
    private var outImageStr = ""
    private var paths = mutableListOf<String>()
    private var drawPaths = mutableListOf<String>()
    private val commitItems = mutableListOf<ItemList>()

    private var pageCount = 0
    private var page = 0 //当前页码

    override fun onToken(token: String) {
        showLoading()
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=exam?.id!!
                    map["commonTypeId"]=exam?.commonTypeId!!
                    map["studentUrl"]=ToolUtils.getImagesStr(urls)
                    mUploadPresenter.commit(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }
    override fun onSuccess(urls: MutableList<String>?) {
    }
    override fun onCommitSuccess() {
        savePaper()
        EventBus.getDefault().post(Constants.RECEIVE_PAPER_COMMIT_EVENT)
        finish()
    }


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        isExpand=true

        exam=intent.getBundleExtra("bundle")?.getSerializable("receivePaper") as PaperList.PaperListBean
        outImageStr = intent.getStringExtra("outImageStr").toString()
        paths = intent.getStringArrayListExtra("imagePaths")!!
        pageCount = paths.size

        course=exam?.subject!!
        commonTypeId=exam?.commonTypeId!!

        //获取之前所有收到的考卷，用来排序
        papers= daoManager?.queryAll(course,commonTypeId) as MutableList<PaperBean>
        paperContents= daoContentManager?.queryAll(course,commonTypeId) as MutableList<PaperContentBean>

        for (i in 0 until paths.size){
            drawPaths.add("$outImageStr/${i+1}/draw.tch")
        }

        startAlarmManager()
    }

    override fun initView() {
        setDrawingTitleClick(false)
        showView(iv_geometry)
        setViewElikUnable(iv_geometry)

        changeExpandView()

        tv_title_a.text=exam?.title
        tv_title_b.text=exam?.title

        changeContent()

        iv_btn.setOnClickListener {
            CommonDialog(this,getCurrentScreenPos()).setContent(R.string.toast_commit_ok).builder().setDialogClickListener(
                object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        showLoading()
                        commit()
                    }
                })
        }
    }

    /**
     * 开启定时任务
     */
    private fun startAlarmManager(){
        val date=Date(exam?.endTime!!)
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
        val intent = Intent(this, AlarmService::class.java)
        intent.action = Constants.ACTION_EXAM_TIME
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, selectLong,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }

    //单屏、全屏内容切换
     override fun changeExpandView(){
        showView(v_content_a,ll_page_content_a,v_empty)
        disMissView(iv_expand_left,iv_expand_right)
        iv_tool_left.visibility= View.INVISIBLE
        iv_tool_right.visibility=View.INVISIBLE
    }

    override fun onPageDown() {
        if (isExpand){
            if (page+2<pageCount){
                page+=2
            }
        }
        else{
            if (page+1<pageCount){
                page+=1
            }
        }
        changeContent()
    }

    override fun onPageUp() {
        if (isExpand){
            if (page>1){
                page-=2
            }
        }
        else{
            if (page>0){
                page-=1
            }
        }
        changeContent()
    }


    //提交
    private fun commit(){
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

    /**
     *  提交完成后，将考卷保存在本地试卷里面
     */
    private fun savePaper(){
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=exam?.id!!
            course=this@PaperExamDrawingActivity.course
            typeId=this@PaperExamDrawingActivity.commonTypeId
            type=exam?.examName
            title=exam?.title
            path=exam?.path
            page=paperContents.size
            index=papers.size
        }
        daoManager?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,exam?.id!!,2,commonTypeId,Gson().toJson(paper))

        for (i in paths.indices){
            //保存本次考试的试卷内容
            val paperContent= PaperContentBean()
                .apply {
                    course=this@PaperExamDrawingActivity.course
                    typeId=this@PaperExamDrawingActivity.commonTypeId
                    contentId=exam?.id!!
                    path=paths[i]
                    drawPath=drawPaths[i]
                    page=paperContents.size+i
                }
            val id=daoContentManager.insertOrReplaceGetId(paperContent)
            DataUpdateManager.createDataUpdate(3,id.toInt(),3,commonTypeId,Gson().toJson(paperContent),paths[i])
        }
    }


    //内容切换
    private fun changeContent(){

        loadImage(page,elik_a!!,v_content_a)
        tv_page_a.text="${page+1}/$pageCount"

        if (isExpand){
            if (page+1<pageCount){
                elik_b?.setPWEnabled(true)
                loadImage(page+1,elik_b!!,v_content_b)
                tv_page_b.text="${page+1+1}/$pageCount"
            }
            else{
                elik_b?.setPWEnabled(false)
                v_content_b.setImageResource(0)
                tv_page_b.text=""
            }
        }

    }

    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view: ImageView) {
        GlideUtils.setImageUrl(this,paths[index],view)
        elik.setLoadFilePath(drawPaths[index],true)
        elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik.saveBitmap(true) {}
            }

        })
    }

    override fun onBackPressed() {
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onMessageEvent(bean: EventBusData) {
        super.onMessageEvent(bean)
        if (bean.event==Constants.EXAM_TIME_EVENT){
            showLoading()
            commit()
        }
    }

}