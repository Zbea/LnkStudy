package com.bll.lnkstudy.ui.activity

import android.media.MediaPlayer
import android.media.MediaRecorder
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_record.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException

class RecordActivity : BaseAppCompatActivity() {

    //语音文件保存路径
    private var path: String? = null

    //语音操作对象
    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private var recordBean: RecordBean? = null
    private var isSave=false

    override fun layoutId(): Int {
        return R.layout.ac_record
    }

    override fun initData() {
        recordBean = intent.getBundleExtra("record")?.getSerializable("record") as RecordBean
        path = File(Constants.RECORD_PATH, "${DateUtils.longToString(recordBean?.date!!)}.amr").toString()
    }

    override fun initView() {
        setPageTitle("录音")
        showSaveView()

        ivSave?.setOnClickListener {
            if (!FileUtils.isExist(path)) {
                showToast("请先录音")
                return@setOnClickListener
            }
            val title=et_title.text.toString()
            if (title.isEmpty()){
                showToast("请输入标题")
                return@setOnClickListener
            }

            isSave=true
            recordBean?.title=title
            recordBean?.path = path
            RecordDaoManager.getInstance().insertOrReplace(recordBean)
            EventBus.getDefault().post(Constants.RECORD_EVENT)
            finish()

        }

        ll_record.setOnClickListener {
            mPlayer?.run {
                release()
                null
            }
            mRecorder = MediaRecorder().apply {
                iv_record.setImageResource(R.mipmap.icon_record_show)
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
                setOutputFile(path)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
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
                showToast("正在录音")
                return@setOnClickListener
            }
            mPlayer?.apply {
                if (isPlaying){
                    iv_play.setImageResource(R.mipmap.icon_record_play)
                    tv_play.text="播放"
                    pause()
                }
                else{
                    iv_play.setImageResource(R.mipmap.icon_record_pause)
                    tv_play.text="暂停"
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
            setDataSource(path)
            setOnCompletionListener {
                iv_play.setImageResource(R.mipmap.icon_record_play)
                tv_play.text="播放"
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
            FileUtils.deleteFile(File(path))
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