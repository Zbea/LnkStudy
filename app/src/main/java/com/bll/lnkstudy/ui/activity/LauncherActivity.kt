package com.bll.lnkstudy.ui.activity

import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity

class LauncherActivity:BaseActivity() {
    override fun layoutId(): Int {
        return R.layout.ac_launcher
    }
    override fun initData() {
    }
    override fun initView() {
        startActivity(Intent(this,AccountLoginActivity::class.java))
    }
}