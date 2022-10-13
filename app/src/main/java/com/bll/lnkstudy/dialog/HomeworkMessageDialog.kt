package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkMessage
import com.bll.lnkstudy.utils.DP2PX

class HomeworkMessageDialog(val context: Context,val screenPos:Int, private val homeworkMessage:  HomeworkMessage) {

    private var dialog:Dialog?=null

    fun builder(): HomeworkMessageDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_homework_message)
        dialog?.show()
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window?.attributes
        layoutParams?.width= DP2PX.dip2px(context,300f)
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,300f))/2
        }
        window?.attributes = layoutParams

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