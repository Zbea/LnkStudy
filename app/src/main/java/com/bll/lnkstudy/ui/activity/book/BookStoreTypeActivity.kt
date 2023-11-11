package com.bll.lnkstudy.ui.activity.book

import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.utils.NetworkUtil
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
        initBookType()
    }

    /**
     * 书城分类
     */
    private fun initBookType(){
        iv_jc?.setOnClickListener {
            customStartActivity(Intent(this, TextbookStoreActivity::class.java))
        }

        iv_gj?.setOnClickListener {
            gotoBookStore(1)
        }

        iv_zrkx?.setOnClickListener {
            gotoBookStore(2)
        }

        iv_shkx?.setOnClickListener {
            gotoBookStore(3)
        }

        iv_swkx?.setOnClickListener {
            gotoBookStore(4)
        }

        iv_ydjk?.setOnClickListener {
            gotoBookStore(5)
        }

        iv_yscn?.setOnClickListener {
            gotoBookStore(6)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtil(this).toggleNetwork(false)
    }
}