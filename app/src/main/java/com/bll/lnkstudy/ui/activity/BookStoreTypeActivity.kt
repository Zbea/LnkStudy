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
            gotoBookStore(getString(R.string.book_tab_gj))
        }

        iv_zrkx.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_zrkx))
        }

        iv_shkx.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_shkx))
        }

        iv_swkx.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_sxkx))
        }

        iv_ydcy.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_ydcy))
        }
    }

}