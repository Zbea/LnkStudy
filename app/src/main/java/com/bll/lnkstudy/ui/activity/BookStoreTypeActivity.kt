package com.bll.lnkstudy.ui.activity

import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import kotlinx.android.synthetic.main.ac_bookstore_type.*

/**
 * 书城分类
 */
class BookStoreTypeActivity:BaseAppCompatActivity() {


    override fun layoutId(): Int {
        return R.layout.ac_bookstore_type
    }

    override fun initData() {

    }

    override fun initView() {
        iv_jc.setOnClickListener {
            customStartActivity(Intent(this,TextBookStoreActivity::class.java))
        }

        iv_gj.setOnClickListener {
            gotoBookStore("古籍")
        }

        iv_zrkx.setOnClickListener {
            gotoBookStore("自然科学")
        }

        iv_shkx.setOnClickListener {
            gotoBookStore("社会科学")
        }

        iv_swkx.setOnClickListener {
            gotoBookStore("思维科学")
        }

        iv_ydcy.setOnClickListener {
            gotoBookStore("运动才艺")
        }
    }

}