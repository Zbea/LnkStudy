package com.bll.lnkstudy.dialog

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX



class NoteAddDialog(private val context: Context) {

    private var dialog:AlertDialog?=null

    fun builder(): NoteAddDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_note_add_module, null)
        dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context, 750f)
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel?.setOnClickListener { dialog?.dismiss() }

        val ll_1=dialog?.findViewById<LinearLayout>(R.id.ll_1)
        ll_1?.setOnClickListener {
            listener?.onClick(1)
        }

        val ll_2=dialog?.findViewById<LinearLayout>(R.id.ll_2)
        ll_2?.setOnClickListener {
            listener?.onClick(2)
        }

        val ll_3=dialog?.findViewById<LinearLayout>(R.id.ll_3)
        ll_3?.setOnClickListener {
            listener?.onClick(3)
        }

        val ll_4=dialog?.findViewById<LinearLayout>(R.id.ll_4)
        ll_4?.setOnClickListener {
            listener?.onClick(4)
        }

        val ll_5=dialog?.findViewById<LinearLayout>(R.id.ll_5)
        ll_5?.setOnClickListener {
            listener?.onClick(5)
        }

        val ll_6=dialog?.findViewById<LinearLayout>(R.id.ll_6)
        ll_6?.setOnClickListener {
            listener?.onClick(6)
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


    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(type:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}