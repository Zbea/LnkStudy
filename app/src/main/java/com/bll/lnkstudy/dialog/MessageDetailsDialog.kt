package com.bll.lnkstudy.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.utils.DP2PX


class MessageDetailsDialog(private val context: Context,val screenPos:Int, private val messageList: MessageList) {

    private var dialog: AlertDialog?=null

    fun builder(): AlertDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_message_details, null)
        dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window?.attributes
        layoutParams?.width=DP2PX.dip2px(context, 480f)
        layoutParams?.height=DP2PX.dip2px(context, 320f)
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,480f))/2
        }
        window?.attributes = layoutParams

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_close)
        ivCancel?.setOnClickListener { dialog?.dismiss() }
        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        val tvTime = dialog?.findViewById<TextView>(R.id.tv_time)
        val tvContent = dialog?.findViewById<TextView>(R.id.tv_content)

        tvName?.text=messageList.name
        tvTime?.text=messageList.createTime
        tvContent?.text=messageList.content


        return dialog
    }




}