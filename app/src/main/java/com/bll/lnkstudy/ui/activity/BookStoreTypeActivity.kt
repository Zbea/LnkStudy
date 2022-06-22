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
        iv_jc.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","教材"))
        }

        iv_gj.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","古籍"))
        }

        iv_zrkx.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","自然科学"))
        }

        iv_shkx.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","社会科学"))
        }

        iv_swkx.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","思维科学"))
        }

        iv_ydcy.setOnClickListener {
            startActivity(Intent(this@BookStoreTypeActivity,BookStoreActivity::class.java)
                .putExtra("title","运动才艺"))
        }

    }
}