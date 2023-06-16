package com.bll.lnkstudy.ui.activity

import android.media.MediaPlayer
import android.media.MediaRecorder
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.homework.RecordBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_record.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException

class RecordActivity : BaseAppCompatActivity() {

    //语音文件保存路径
    private var pathFile: String? = null
    //语音操作对象
    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private var recordBean: RecordBean? = null
    private var isSave=false

    override fun layoutId(): Int {
        return R.layout.ac_record
    }

    override fun initData() {
        recordBean = intent.getBundleExtra("recordBundle")?.getSerializable("record") as RecordBean
        val path=FileAddress().getPathRecord(recordBean?.course!!,recordBean?.typeId!!)
        if (!File(path).exists())
            File(path).mkdir()
        pathFile = File(path, "${DateUtils.longToString(recordBean?.date!!)}.mp3").path
    }

    override fun initView() {
        setPageTitle(R.string.record_title_str)
        showSaveView()

        iv_back?.setOnClickListener {
            finish()
            FileUtils.deleteFile(File(pathFile))
        }

        iv_save?.setOnClickListener {
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

            isSave=true
            recordBean?.title=title
            recordBean?.path = pathFile
            val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
            //创建增量数据
            DataUpdateManager.createDataUpdate(2,id.toInt(),2,recordBean?.typeId!!,3
                ,Gson().toJson(recordBean),pathFile!!)

            EventBus.getDefault().post(Constants.RECORD_EVENT)
            finish()
        }

        ll_record.setOnClickListener {
            hideKeyboard()
            mPlayer?.run {
                release()
                null
            }
            mRecorder = MediaRecorder().apply {
                iv_record.setImageResource(R.mipmap.icon_record_show)
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setOutputFile(pathFile)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                try {
                    prepare()//准备
                    start()//开始录音
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
        }

        ll_record_stop.setOnClickListener {
            mRecorder?.apply {
                iv_record.setImageResource(R.mipmap.icon_record_file)
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
                stop()
                release()
                mRecorder=null
                startPrepare()
            }
        }

        ll_record_play.setOnClickListener {
            if(mRecorder!=null){
                showToast(R.string.toast_recording)
                return@setOnClickListener
            }
            mPlayer?.apply {
                if (isPlaying){
                    iv_play.setImageResource(R.mipmap.icon_record_play)
                    tv_play.setText(R.string.play)
                    pause()
                }
                else{
                    iv_play.setImageResource(R.mipmap.icon_record_pause)
                    tv_play.setText(R.string.pause)
                    start()
                }

            }
        }

        ll_record_backward.setOnClickListener {
            backWard()
        }

        ll_record_forward.setOnClickListener {
            forWard()
        }

    }

    //播放更新准备
    private fun startPrepare(){
        mPlayer = MediaPlayer().apply {
            setDataSource(pathFile)
            setOnCompletionListener {
                iv_play.setImageResource(R.mipmap.icon_record_play)
                tv_play.setText(R.string.play)
            }
            prepare()
        }
    }

    //快进1秒
    private fun forWard(){
        mPlayer?.apply {
            if (isPlaying) seekTo(currentPosition + 1000)
        }
    }

    //后退一秒
    private fun backWard(){
        mPlayer?.apply {
            if (isPlaying){
                var position = currentPosition
                if(position > 1000){
                    position-=1000
                }else{
                    position = 0
                }
                seekTo(position)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //未保存清理掉录音原件
        if (!isSave){
            FileUtils.deleteFile(File(pathFile))
        }
        mRecorder?.run {
            stop()
            release()
            null
        }

        mPlayer?.run {
            release()
            null
        }
    }

}