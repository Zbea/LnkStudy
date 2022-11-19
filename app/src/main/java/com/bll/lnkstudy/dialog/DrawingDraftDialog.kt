package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.Gravity
import android.view.PWDrawObjectHandler
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioGroup
import com.android.internal.widget.PreferenceImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SPUtil

class DrawingDraftDialog(val context: Context, val screenPos:Int) {

    private var dialog:Dialog?=null
    private var eink:EinkPWInterface?=null
    private var layoutParams:WindowManager.LayoutParams?=null
    private var isRight=true
    private val paths= mutableListOf<String>()

    fun builder(): DrawingDraftDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.ac_drawing_draft)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        layoutParams=window?.attributes
        layoutParams?.width=Constants.WIDTH
        layoutParams?.y=DP2PX.dip2px(context,32f)
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.RIGHT
        window?.attributes = layoutParams


        val iv_change=dialog?.findViewById<ImageView>(R.id.iv_change)
        iv_change?.setOnClickListener {
            dialog?.dismiss()
            isRight = !isRight
            if (isRight) {
                layoutParams?.gravity = Gravity.BOTTOM or Gravity.RIGHT
            } else {
                layoutParams?.gravity = Gravity.BOTTOM or Gravity.LEFT
            }
            dialog?.window?.attributes = layoutParams
            val pos=SPUtil.getInt("draft")
            onClick(pos)
            dialog?.show()
        }
        val iv_clear=dialog?.findViewById<ImageView>(R.id.iv_clear)
        iv_clear?.setOnClickListener {
            eink?.clearContent(null,true,true)
            if (eink?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                eink?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
        }
        val ivCancel=dialog?.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener {
            dismiss()
        }

        val rg_group=dialog?.findViewById<RadioGroup>(R.id.rg_group)
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

        for (i in 0..4){
            paths.add(FileAddress().getPathDraft()+"/$i.tch")
        }

        val v_content=dialog?.findViewById<PreferenceImageView>(R.id.v_content)
        eink=v_content?.pwInterFace

        val pos=SPUtil.getInt("draft")
        onClick(pos)

        dialog?.show()
        return this
    }

    private fun onClick(index:Int){
        changeEilk(paths[index])
        SPUtil.putObj("draft",index)
    }

    private fun changeEilk(path:String){
        eink?.setPWEnabled(true)
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

    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }



}