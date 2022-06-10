package com.bll.lnkstudy.ui.activity

import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import kotlinx.android.synthetic.main.ac_bookstore_type.*

/**
 * 书城分类
 */
class BookStoreTypeActivity:BaseActivity() {


    override fun layoutId(): Int {
        return R.layout.ac_bookstore_type
    }

    override fun initData() {

    }

    override fun initView() {
        tv_jc.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java))
        }
    }
}