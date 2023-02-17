package com.bll.lnkstudy.ui.activity

import android.widget.MediaController
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import kotlinx.android.synthetic.main.ac_teach.*


class TeachActivity:BaseAppCompatActivity() {

    private var teach: TeachingVideoList.ItemBean?=null

    override fun layoutId(): Int {
        return R.layout.ac_teach
    }

    override fun initData() {
        teach= intent.getBundleExtra("bundle")?.getSerializable("teach") as TeachingVideoList.ItemBean

    }

    override fun initView() {
        setPageTitle(teach?.videoName!!)

        val mediacontroller = MediaController(this)
        videoView.setMediaController(mediacontroller)
        videoView.setVideoPath(teach?.bodyUrl)

    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }

}