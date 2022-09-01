package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkMessage

class HomeworkMessageDialog(val context: Context, private val homeworkMessage:  HomeworkMessage) {

    private var dialog:Dialog?=null

    fun builder(): HomeworkMessageDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_homework_message)
        dialog?.show()
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle=dialog?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text=homeworkMessage.title


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