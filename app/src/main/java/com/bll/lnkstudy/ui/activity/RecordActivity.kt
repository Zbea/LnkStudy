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
            var title=et_title.text.toString()
            if (title.isNullOrEmpty()){
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
            mRecorder = MediaRecorder()
            iv_record.setImageResource(R.mipmap.icon_record_show)
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            mRecorder?.setOutputFile(path)
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            try {
                mRecorder?.prepare()//准备
                mRecorder?.start()//开始录音
            } catch (e: IOException) {
                e.printStackTrace();
            }

        }
        ll_record_stop.setOnClickListener {
            if (mRecorder != null) {
                iv_record.setImageResource(R.mipmap.icon_record_file)
                mRecorder?.setOnErrorListener(null)
                mRecorder?.setOnInfoListener(null)
                mRecorder?.setPreviewDisplay(null)
                mRecorder?.stop()
//                try {
//                    mRecorder?.stop()
//                } catch (e:IllegalStateException) {
//                    // TODO 如果当前java状态和jni里面的状态不一致，
//                    mRecorder = null
//                    mRecorder = MediaRecorder()
//                }
                mRecorder?.release()
                mRecorder = null

                prepare()
            }
        }
        ll_record_play.setOnClickListener {
            if(mRecorder!=null){
                showToast("正在录音")
                return@setOnClickListener
            }
            if (mPlayer!=null){
                if (mPlayer?.isPlaying==true){
                    mPlayer?.pause()
                }
                else{
                    mPlayer?.start()
                }
                changePlayView()
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
    private fun prepare(){
        mPlayer = MediaPlayer()
        mPlayer?.setDataSource(path)
        mPlayer?.setOnCompletionListener {
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.text="播放"
        }
        mPlayer?.prepare()
    }

    private fun changePlayView(){
        if (mPlayer?.isPlaying==true){
            iv_play.setImageResource(R.mipmap.icon_record_pause)
            tv_play.text="暂停"
        }
        else{
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.text="播放"
        }
    }

    //快进1秒
    private fun forWard(){
        if(mPlayer != null&&mPlayer?.isPlaying==true){
            var position = mPlayer?.currentPosition
            if (position != null) {
                mPlayer?.seekTo(position + 1000)
            }
        }
    }

    //后退一秒
    private fun backWard(){
        if(mPlayer != null&&mPlayer?.isPlaying==true){
            var position = mPlayer?.currentPosition
            if(position!! > 1000){
                position-=1000
            }else{
                position = 0
            }
            mPlayer?.seekTo(position)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //未保存清理掉录音原件
        if (!isSave){
            FileUtils.deleteFile(File(path))
        }

        if (mRecorder!=null){
            mRecorder?.stop()
            mRecorder?.release()
            mRecorder = null
        }
        if (mPlayer!=null){
            mPlayer?.release()
            mPlayer = null
        }
    }

}