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
            gotoBookStore(0)
        }

        iv_gj.setOnClickListener {
            gotoBookStore(1)
        }

        iv_zrkx.setOnClickListener {
            gotoBookStore(2)
        }

        iv_shkx.setOnClickListener {
            gotoBookStore(3)
        }

        iv_swkx.setOnClickListener {
            gotoBookStore(4)
        }

        iv_ydcy.setOnClickListener {
            gotoBookStore(5)
        }

    }

    private fun gotoBookStore(type: Int){
        val intent=Intent(this,BookStoreActivity::class.java)
        intent.flags = type
        customStartActivity(intent)
    }
}