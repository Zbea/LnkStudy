package com.bll.lnkstudy.ui.activity

import cn.jzvd.Jzvd
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import kotlinx.android.synthetic.main.ac_teach.*
import java.util.*


class TeachActivity:BaseAppCompatActivity() {

    private var teach: TeachingVideoList.ItemBean?=null
    //将长度转换为时间
    private val mFormatBuilder = StringBuilder()
    private val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
    private var timer=Timer()
    private var isResume=false

    override fun layoutId(): Int {
        return R.layout.ac_teach
    }

    override fun initData() {
        teach= intent.getBundleExtra("bundle")?.getSerializable("teach") as TeachingVideoList.ItemBean
    }

    override fun initView() {
        setPageTitle(teach?.videoName!!)

        tv_info.text=teach?.info

        videoplayer.setUp(teach?.bodyUrl, "")

//        videoView.setVideoPath(teach?.bodyUrl)
//        videoView.setOnPreparedListener {
//            sb_bar.max=videoView.duration
//            tv_max.text=stringForTime(videoView.duration)
//        }
//
//        videoView.setOnCompletionListener {
//            iv_play.setImageResource(R.mipmap.icon_record_pause)
//            videoView.stopPlayback()
//            SFCommand.stopVideoDisplay(true)
//            isResume=true
//            runOnUiThread {
//                sb_bar.progress=0
//                tv_current.text="00:00"
//            }
//        }
//
//        sb_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
//            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//            }
//            override fun onStartTrackingTouch(p0: SeekBar?) {
//            }
//            override fun onStopTrackingTouch(p0: SeekBar?) {
//                if(isLoadVideoFinish()){
//                    videoView.seekTo(p0?.progress!!)
//                }
//            }
//        })
//
//        iv_play.setOnClickListener {
//            if(!isLoadVideoFinish()){
//                return@setOnClickListener
//            }
//            if (videoView.isPlaying){
//                iv_play.setImageResource(R.mipmap.icon_record_play)
//                videoView.pause()
//                SFCommand.stopVideoDisplay(true)
//            }
//            else{
//                iv_play.setImageResource(R.mipmap.icon_record_pause)
//                if (isResume){
//                    isResume=false
//                    videoView.resume()
//                }
//                else{
//                    videoView.start()
//                }
//                SFCommand.stopVideoDisplay(false)
//            }
//        }
//
//        iv_backward.setOnClickListener {
//            if(!isLoadVideoFinish()){
//                return@setOnClickListener
//            }
//            val time=videoView.currentPosition
//            if (time<5000){
//                videoView.seekTo(0)
//            }
//            else{
//                videoView.seekTo(time-5000)
//            }
//        }
//
//        iv_speed.setOnClickListener {
//            if(!isLoadVideoFinish()){
//                return@setOnClickListener
//            }
//            val time=videoView.currentPosition
//            if (time+5000>videoView.duration){
//                videoView.seekTo(videoView.duration)
//            }
//            else{
//                videoView.seekTo(time+5000)
//            }
//        }
//
//        val task = object : TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    if (videoView.isPlaying){
//                        val current = videoView.currentPosition
//                        sb_bar.progress=current
//                        tv_current.text=stringForTime(current)
//                    }
//                }
//            }
//        }
//        timer.schedule(task,1000,1000)
    }

    /**
     * 视频是否已经加载完成
     */
    private fun isLoadVideoFinish():Boolean{
        return videoView.duration>0
    }

    //将长度转换为时间
    private fun stringForTime(timeMs:Int):String {
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.suspend()
        timer.cancel()
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }


}