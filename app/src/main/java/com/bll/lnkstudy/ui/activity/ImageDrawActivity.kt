package com.bll.lnkstudy.ui.activity

import android.graphics.BitmapFactory
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import kotlinx.android.synthetic.main.ac_image_draw.*

class ImageDrawActivity:BaseActivity() ,View.OnClickListener{
    private var type=""
    private var outImageStr=""
    private var imageStr=""
    private var resId=0
    private var elik: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_image_draw
    }

    override fun initData() {
        type=intent.getStringExtra("type").toString()
        outImageStr=intent.getStringExtra("outImageStr").toString()
        if (type == "note")
        {
            resId=intent.getIntExtra("resId",0)
            if (resId!=0){
                iv_content.setImageResource(resId)
            }
        }
        if (type=="testPaper")
        {
            imageStr=intent.getStringExtra("imageStr").toString()
            iv_content.setImageBitmap(BitmapFactory.decodeFile(imageStr))
        }


    }

    override fun initView() {

        tv_save.setOnClickListener(this)
        iv_fine.setOnClickListener (this)
        iv_thick.setOnClickListener (this)
        iv_clear.setOnClickListener (this)
        iv_clear_all.setOnClickListener (this)

        elik=iv_content.pwInterFace
        elik?.setLoadFilePath("$outImageStr/draw.tch",true)


    }

    override fun onClick(view: View?) {

        if (view==tv_save){
            elik?.saveBitmap(true) {
                showLog(it)
                showToast("保存成功")
                finish()
            }
        }
        if (view==iv_fine){
            if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik?.penSettingWidth=2
        }
        if (view==iv_thick){
            if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik?.penSettingWidth=6
        }
        if (view==iv_clear){
            elik?.drawObjectType=PWDrawObjectHandler.DRAW_OBJ_ERASE
            elik?.penEraseWidth = 6
        }
        if (view==iv_clear_all){
            elik?.clearContent(null,true,true)
            elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
        }

    }


}