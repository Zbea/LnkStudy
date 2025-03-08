package com.bll.lnkstudy.ui.activity

import android.media.MediaPlayer
import android.media.MediaRecorder
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList.MessageBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_record.et_title
import kotlinx.android.synthetic.main.ac_record.iv_play
import kotlinx.android.synthetic.main.ac_record.ll_record
import kotlinx.android.synthetic.main.ac_record.ll_record_backward
import kotlinx.android.synthetic.main.ac_record.ll_record_forward
import kotlinx.android.synthetic.main.ac_record.ll_record_play
import kotlinx.android.synthetic.main.ac_record.ll_record_stop
import kotlinx.android.synthetic.main.ac_record.tv_play
import kotlinx.android.synthetic.main.ac_record.tv_time
import kotlinx.android.synthetic.main.common_title.tv_setting
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Timer
import java.util.TimerTask

class RecordActivity : BaseAppCompatActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter: FileUploadPresenter
    private var homeworkType: HomeworkTypeBean? = null
    private var isHomework=false
    private var messageBean:MessageBean?=null
    //语音文件保存路径
    private var pathFile: String? = null
    //语音操作对象
    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private var recordBean: RecordBean? = null
    private var isSave=false
    private var second=0
    private var timer: Timer?=null
    private var commitPaths= mutableListOf<String>()

    override fun onToken(token: String) {
        showLoading()
        FileImageUploadManager(token,commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=messageBean?.contendId!!
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = messageBean?.typeId!!
                    mUploadPresenter.commit(map)
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
        recordBean?.isHomework=false
        val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
        //创建增量数据
        DataUpdateManager.createDataUpdateState(2,id.toInt(),2,recordBean?.homeworkTypeId!!,3,Gson().toJson(recordBean),pathFile!!)
        EventBus.getDefault().post(Constants.HOMEWORK_MESSAGE_COMMIT_EVENT)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_record
    }

    override fun initData() {
        initChangeScreenData()
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)
        val index = intent.getIntExtra("messageIndex", DEFAULT_PAGE)
        isHomework=index>=0
        if (isHomework){
            messageBean=homeworkType?.messages!![index]
            recordBean=RecordDaoManager.getInstance().queryByContendId(messageBean?.contendId!!)
        }

        if (recordBean!=null){
            pathFile=recordBean?.path
            second= recordBean?.second!!
        }
        else{
            val time = System.currentTimeMillis()
            recordBean = RecordBean()
            recordBean?.date = time
            recordBean?.course = homeworkType?.course!!
            recordBean?.homeworkTypeId=homeworkType?.typeId!!
            recordBean?.typeName=homeworkType?.name
            if (isHomework){
                recordBean?.isHomework=true
                recordBean?.title=messageBean?.title
                recordBean?.contendId= messageBean?.contendId
            }
            val path=FileAddress().getPathHomework(recordBean?.course!!,recordBean?.homeworkTypeId!!)
            if (!File(path).exists())
                File(path).mkdirs()
            pathFile = File(path, "${DateUtils.longToString(recordBean?.date!!)}.mp3").path
            recordBean?.path=pathFile
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(R.string.record_title_str)
        setPageSetting(if (isHomework) "提交" else "保存")
        et_title.setText(recordBean?.title)
        tv_time.text= DateUtils.secondToString(second)

        tv_setting?.setOnClickListener {
            hideKeyboard()
            if (!FileUtils.isExist(pathFile)) {
                showToast(R.string.toast_record)
                return@setOnClickListener
            }
            val title=et_title.text.toString()
            if (title.isEmpty()){
                showToast(R.string.toast_input_title)
                return@setOnClickListener
            }

            if (isHomework){
                if (NetworkUtil(this).isNetworkConnected()) {
                    showLoading()
                    if (messageBean?.submitState==0){
                        commitPaths.add(pathFile!!)
                        mUploadPresenter.getToken()
                    }
                    else{
                        //不提交作业直接完成
                        val map = HashMap<String, Any>()
                        map["studentTaskId"] = messageBean?.contendId!!
                        mUploadPresenter.commitHomework(map)
                    }
                } else {
                    showToast("网络连接失败，无法提交")
                }
            }
            else{
                isSave=true
                recordBean?.title=title
                val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
                //创建增量数据
                DataUpdateManager.createDataUpdateState(2,id.toInt(),2,recordBean?.homeworkTypeId!!,3,Gson().toJson(recordBean),pathFile!!)
                EventBus.getDefault().post(Constants.RECORD_EVENT)
                finish()
            }
        }

        ll_record.setOnClickListener {
            hideKeyboard()
            if (mPlayer?.isPlaying==true){
                iv_play.setImageResource(R.mipmap.icon_record_play)
                tv_play.setText(R.string.play)
                releaseMediaPlayer()
                timer?.cancel()
            }
            if (mRecorder==null){
                mRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                    setOutputFile(pathFile)
                    prepare()//准备
                    start()//开始录音
                }
                second=0
                startTimer(1)
            }
        }

        ll_record_stop.setOnClickListener {
            if (mRecorder==null){
                return@setOnClickListener
            }
            recordBean?.second=second
            releaseRecorder()
            timer?.cancel()
        }

        ll_record_play.setOnClickListener {
            if(mRecorder!=null){
                showToast(R.string.toast_recording)
                return@setOnClickListener
            }
            if (mPlayer==null){
                mPlayer = MediaPlayer().apply {
                    setDataSource(pathFile)
                    setOnCompletionListener {
                        iv_play.setImageResource(R.mipmap.icon_record_play)
                        tv_play.setText(R.string.play)
                        second=recordBean?.second!!
                        tv_time.text= DateUtils.secondToString(second)
                        timer?.cancel()
                    }
                    prepare()
                }
            }

            mPlayer?.apply {
                if (isPlaying){
                    iv_play.setImageResource(R.mipmap.icon_record_play)
                    tv_play.setText(R.string.play)
                    pause()
                    timer?.cancel()
                }
                else{
                    iv_play.setImageResource(R.mipmap.icon_record_pause)
                    tv_play.setText(R.string.pause)
                    start()
                    startTimer(2)
                }
            }
        }

        ll_record_backward.setOnClickListener {
            if(mRecorder!=null){
                showToast(R.string.toast_recording)
                return@setOnClickListener
            }
            backWard()
        }

        ll_record_forward.setOnClickListener {
            if(mRecorder!=null){
                showToast(R.string.toast_recording)
                return@setOnClickListener
            }
            forWard()
        }

    }

    private fun startTimer(type:Int){
        Thread {
            timer= Timer()
            timer!!.schedule(object: TimerTask() {
                override fun run() {
                    if (type==1){
                        second+=1
                    }
                    else{
                        second-=1
                    }
                    runOnUiThread {
                        tv_time.text= DateUtils.secondToString(second)
                    }
                }
            } ,1000,1000)
        }.start()
    }

    private fun releaseRecorder(){
        if (mRecorder!=null){
            mRecorder?.stop()
            mRecorder?.release()
            mRecorder=null
        }
    }

    private fun releaseMediaPlayer(){
        if (mPlayer!=null){
            mPlayer?.pause()
            mPlayer?.release()
            mPlayer=null
        }
    }

    //快进1秒
    private fun forWard(){
        if (mPlayer?.isPlaying==true){
            if (second>1){
                timer?.cancel()
                mPlayer?.seekTo(mPlayer?.currentPosition!!+ 1000)
                second-=1
                tv_time.text=DateUtils.secondToString(second)
                startTimer(2)
            }
        }
    }

    //后退一秒
    private fun backWard(){
        if (mPlayer?.isPlaying==true){
            if (second<recordBean?.second!!){
                timer?.cancel()
                mPlayer?.seekTo(mPlayer?.currentPosition!! -1000)
                second+=1
                tv_time.text=DateUtils.secondToString(second)
                startTimer(2)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isHomework){
            if (FileUtils.isExist(pathFile)){
                val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
                //创建增量数据
                DataUpdateManager.createDataUpdateState(2,id.toInt(),2,recordBean?.homeworkTypeId!!,3,Gson().toJson(recordBean),pathFile!!)
            }
        }
        else{
            if (!isSave){
                FileUtils.deleteFile(File(pathFile))
            }
        }
        timer?.cancel()
        releaseRecorder()
        releaseMediaPlayer()
    }

}