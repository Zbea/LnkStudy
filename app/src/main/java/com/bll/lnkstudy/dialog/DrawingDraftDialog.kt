package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.internal.widget.PreferenceImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R

class DrawingDraftDialog(val context: Context, val screenPos:Int) {

    private var dialog:Dialog?=null

    fun builder(): DrawingDraftDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_draft)
        dialog?.show()
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window?.attributes
        layoutParams.width=Constants.WIDTH
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
        window?.attributes = layoutParams

        val ivCancel=dialog?.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener {
            dismiss()
        }
        val tv_1=dialog?.findViewById<TextView>(R.id.tv_1)
        val tv_2=dialog?.findViewById<TextView>(R.id.tv_2)
        val tv_3=dialog?.findViewById<TextView>(R.id.tv_3)
        val tv_4=dialog?.findViewById<TextView>(R.id.tv_4)
        val tv_5=dialog?.findViewById<TextView>(R.id.tv_5)

        val v_content_1=dialog?.findViewById<PreferenceImageView>(R.id.v_content_1)
        v_content_1?.pwInterFace
        val v_content_2=dialog?.findViewById<PreferenceImageView>(R.id.v_content_2)
        v_content_2?.pwInterFace
        val v_content_3=dialog?.findViewById<PreferenceImageView>(R.id.v_content_3)
        v_content_3?.pwInterFace
        val v_content_4=dialog?.findViewById<PreferenceImageView>(R.id.v_content_4)
        v_content_4?.pwInterFace
        val v_content_5=dialog?.findViewById<PreferenceImageView>(R.id.v_content_5)
        v_content_5?.pwInterFace

        tv_1?.setOnClickListener {
            v_content_1?.visibility= View.VISIBLE
            v_content_2?.visibility= View.GONE
            v_content_3?.visibility= View.GONE
            v_content_4?.visibility= View.GONE
            v_content_5?.visibility= View.GONE
        }

        tv_2?.setOnClickListener {
            v_content_1?.visibility= View.GONE
            v_content_2?.visibility= View.VISIBLE
            v_content_3?.visibility= View.GONE
            v_content_4?.visibility= View.GONE
            v_content_5?.visibility= View.GONE
        }

        tv_3?.setOnClickListener {
            v_content_1?.visibility= View.GONE
            v_content_2?.visibility= View.GONE
            v_content_3?.visibility= View.VISIBLE
            v_content_4?.visibility= View.GONE
            v_content_5?.visibility= View.GONE
        }

        tv_4?.setOnClickListener {
            v_content_1?.visibility= View.GONE
            v_content_2?.visibility= View.GONE
            v_content_3?.visibility= View.GONE
            v_content_4?.visibility= View.VISIBLE
            v_content_5?.visibility= View.GONE
        }

        tv_5?.setOnClickListener {
            v_content_1?.visibility= View.GONE
            v_content_2?.visibility= View.GONE
            v_content_3?.visibility= View.GONE
            v_content_4?.visibility= View.GONE
            v_content_5?.visibility= View.VISIBLE
        }

        return this
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