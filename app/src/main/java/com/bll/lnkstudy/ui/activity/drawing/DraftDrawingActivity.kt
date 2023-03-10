package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.Gravity
import android.view.PWDrawObjectHandler
import android.widget.RadioButton
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_drawing_draft.*

/**
 * 草稿纸
 */
class DraftDrawingActivity:BaseDrawingActivity(){

    private var eink: EinkPWInterface?=null
    private val paths= mutableListOf<String>()
    private val pos= SPUtil.getInt("draft")

    override fun layoutId(): Int {
        return R.layout.ac_drawing_draft
    }

    override fun initData() {
        for (i in 0..4){
            paths.add(FileAddress().getPathDraft()+"/$i.tch")
        }
    }

    override fun initView() {

        val layoutParams=window?.attributes
        layoutParams?.width= Constants.WIDTH
        layoutParams?.y= DP2PX.dip2px(this,32f)
        layoutParams?.gravity = Gravity.BOTTOM
        window?.attributes = layoutParams

        eink=v_content?.pwInterFace
        onClick(pos)
        (rg_group.getChildAt(pos) as RadioButton).isChecked=true

        iv_clear?.setOnClickListener {
            eink?.clearContent(null,true,true)
            if (eink?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                eink?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
        }

        iv_change?.setOnClickListener {
            when(getCurrentScreenPos()){
                0->{
                    moveToScreen(2)
                }
                1->{
                    moveToScreen(2)
                }
                2->{
                    moveToScreen(1)
                }
            }
        }

        iv_cancel?.setOnClickListener {
            finish()
        }

        rg_group?.setOnCheckedChangeListener { p0, id ->
            when(id){
                R.id.rb_1->{
                    onClick(0)
                }
                R.id.rb_2->{
                    onClick(1)
                }
                R.id.rb_3->{
                    onClick(2)
                }
                R.id.rb_4->{
                    onClick(3)
                }
                R.id.rb_5->{
                    onClick(4)
                }
            }
        }

    }

    private fun onClick(index:Int){
        changeEilk(paths[index])
        SPUtil.putObj("draft",index)
    }

    private fun changeEilk(path:String){
        eink?.setLoadFilePath(path, true)
        eink?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                eink?.saveBitmap(true) {}
            }
        })
    }


}