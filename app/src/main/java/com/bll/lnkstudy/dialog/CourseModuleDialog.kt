package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX


class CourseModuleDialog(private val context: Context,private val screenPos:Int) {

    fun builder(): CourseModuleDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_course_module)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,800f))/2
        }
        dialog.show()

        val ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        val tvModule1 = dialog.findViewById<LinearLayout>(R.id.ll_1)
        tvModule1?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(0)
        }

        val tvModule2 = dialog?.findViewById<LinearLayout>(R.id.ll_2)
        tvModule2?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(1)
        }

        val tvModule3 = dialog?.findViewById<LinearLayout>(R.id.ll_3)
        tvModule3?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(2)
        }

        val tvModule4 = dialog?.findViewById<LinearLayout>(R.id.ll_4)
        tvModule4?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(3)
        }

        val tvModule5 = dialog?.findViewById<LinearLayout>(R.id.ll_5)
        tvModule5?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(4)
        }

        val tvModule6 = dialog?.findViewById<LinearLayout>(R.id.ll_6)
        tvModule6?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(5)
        }

        ivCancel?.setOnClickListener { dialog?.dismiss() }


        return this
    }





    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onClick(type:Int)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}