package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.R


class MessageSendDialog(private val context: Context) {

    private var dialog: Dialog?=null

    fun builder(): MessageSendDialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_message_send)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val tvOK = dialog?.findViewById<TextView>(R.id.tv_ok)
        val tvCancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val et_content = dialog?.findViewById<EditText>(R.id.et_content)


        tvCancel?.setOnClickListener { dismiss() }
        tvOK?.setOnClickListener {
            val contentStr=et_content?.text.toString()
            if (contentStr.isNotEmpty())
            {
                dismiss()
                listener?.onSend(contentStr)
            }
        }

        return this
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onSend(contentStr:String)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }


}