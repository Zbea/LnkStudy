package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity

class RemoteSchoolActivity :BaseAppCompatActivity() {
    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle("远程在校")
    }
}