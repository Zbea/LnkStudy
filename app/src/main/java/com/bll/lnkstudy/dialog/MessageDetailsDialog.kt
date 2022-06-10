package com.bll.lnkstudy.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageBean
import com.bll.lnkstudy.utils.DP2PX


class MessageDetailsDialog(private val context: Context, private val messageBean: MessageBean) {

    private var dialog: AlertDialog?=null

    fun builder(): AlertDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_message_details, null)
        dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(DP2PX.dip2px(context, 480f),DP2PX.dip2px(context, 320f))

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_close)
        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        val tvTime = dialog?.findViewById<TextView>(R.id.tv_time)
        val tvContent = dialog?.findViewById<TextView>(R.id.tv_content)

        tvName?.text=messageBean.name
        tvTime?.text=messageBean.createTime
        tvContent?.text=messageBean.content

        ivCancel?.setOnClickListener { dialog?.dismiss() }


        return dialog
    }


}