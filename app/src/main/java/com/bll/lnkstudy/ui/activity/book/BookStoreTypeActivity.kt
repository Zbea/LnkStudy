package com.bll.lnkstudy.ui.activity.book

import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_gj
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_jc
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_shkx
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_swkx
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_ydjk
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_yscn
import kotlinx.android.synthetic.main.ac_bookstore_type.iv_zrkx

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

        iv_yscn?.setOnClickListener {
            gotoBookStore(5)
        }
        iv_ydjk?.setOnClickListener {
            gotoBookStore(6)
        }
    }

}