package com.bll.lnkstudy.ui.activity

import cn.jzvd.Jzvd
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.mvp.model.TeachList
import kotlinx.android.synthetic.main.ac_teach.*


class TeachActivity:BaseActivity() {

    private var teach:TeachList?=null

    override fun layoutId(): Int {
        return R.layout.ac_teach
    }

    override fun initData() {
        teach= intent.getBundleExtra("teachs")?.getSerializable("teach") as TeachList

    }

    override fun initView() {
        setPageTitle(teach?.name!!)

        jz_vd.setUp(teach?.address, "")
        jz_vd.startPreloading(); //开始预加载，加载完等待播放
        jz_vd.startVideoAfterPreloading(); //如果预加载完会开始播放，如果未加载则开始加载
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