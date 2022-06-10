package com.bll.lnkstudy.dialog

import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX


class CourseModuleDialog(private val context: Context) {

    fun builder(): CourseModuleDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_course_module, null)
        var dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context, 680f)
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        val tvModule1 = dialog?.findViewById<TextView>(R.id.tv_module1)
        tvModule1?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(0)
        }

        val tvModule2 = dialog?.findViewById<TextView>(R.id.tv_module2)
        tvModule2?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(1)
        }

        val tvModule3 = dialog?.findViewById<TextView>(R.id.tv_module3)
        tvModule3?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(2)
        }

        val tvModule4 = dialog?.findViewById<TextView>(R.id.tv_module4)
        tvModule4?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(3)
        }

        val tvModule5 = dialog?.findViewById<TextView>(R.id.tv_module5)
        tvModule5?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(4)
        }

        val tvModule6 = dialog?.findViewById<TextView>(R.id.tv_module6)
        tvModule6?.setOnClickListener {
            dialog?.dismiss()
            if (listener!=null)
                listener?.onClick(5)
        }

        ivCancel?.setOnClickListener { dialog?.dismiss() }


        return this
    }





    private var listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(type:Int)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}