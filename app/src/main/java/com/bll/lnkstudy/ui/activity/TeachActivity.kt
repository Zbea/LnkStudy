package com.bll.lnkstudy.ui.activity

import cn.jzvd.Jzvd
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import kotlinx.android.synthetic.main.ac_teach.tv_info
import kotlinx.android.synthetic.main.ac_teach.videoplayer


class TeachActivity:BaseAppCompatActivity() {

    private var teach: TeachingVideoList.VideoBean?=null

    override fun layoutId(): Int {
        return R.layout.ac_teach
    }

    override fun initData() {
        teach= intent.getBundleExtra("bundle")?.getSerializable("teach") as TeachingVideoList.VideoBean
    }

    override fun initView() {
        setPageTitle(teach?.videoName!!)

        tv_info.text=teach?.info
        videoplayer.setUp(teach?.bodyUrl, "")
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

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.WIFI_CONNECTION_FAIL_EVENT){
            showToast("WIFI已关闭，无法播放视频")
            finish()
        }
    }

}