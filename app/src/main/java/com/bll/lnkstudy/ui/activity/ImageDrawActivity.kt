package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import kotlinx.android.synthetic.main.ac_image_draw.*

class ImageDrawActivity:BaseActivity(){

    private var imageStr=""

    override fun layoutId(): Int {
        return R.layout.ac_image_draw
    }

    override fun initData() {
        imageStr=intent.getStringExtra("image").toString()
    }

    override fun initView() {
        iv_content.setLoadFilePath(imageStr,true)
    }


}