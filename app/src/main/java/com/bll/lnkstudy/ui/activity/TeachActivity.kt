package com.bll.lnkstudy.ui.activity

import android.net.Uri
import android.widget.MediaController
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.DataList
import kotlinx.android.synthetic.main.ac_teach.*


class TeachActivity:BaseAppCompatActivity() {

    private var teach: DataList?=null

    override fun layoutId(): Int {
        return R.layout.ac_teach
    }

    override fun initData() {
        teach= intent.getBundleExtra("bundle")?.getSerializable("teach") as DataList

    }

    override fun initView() {
        setPageTitle(teach?.name!!)

        val mediacontroller = MediaController(this)
        videoView.setMediaController(mediacontroller)
        if (teach?.id==0){
            val uri = "android.resource://" + packageName + "/" + R.raw.video
            videoView.setVideoURI(Uri.parse(uri))
        }
        else{
            videoView.setVideoPath(teach?.address)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }

}