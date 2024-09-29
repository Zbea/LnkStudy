package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX


class CommonDialog(private val context: Context,private val screenPos:Int) {

    private var dialog: Dialog? = null
    private var titleStr = ""
    private var contentStr = "" //提示文案
    private var contentStrId = 0 //提示文案
    private var cancelStr = "取消" //取消文案
    private var okStr = "确认" //确认文案

    constructor(context: Context):this(context,0)

    fun setTitle(title: String): CommonDialog {
        this.titleStr = title
        return this
    }

    fun setContent(content: String): CommonDialog {
        this.contentStr = content
        return this
    }

    fun setContent(contentId: Int): CommonDialog {
        this.contentStrId = contentId
        return this
    }

    fun setCancel(cancel: String): CommonDialog {
        cancelStr = cancel
        return this
    }

    fun setOk(ok: String): CommonDialog {
        okStr = ok
        return this
    }

    fun builder(): CommonDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_com)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450F))/2
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450F))/2
        }
        dialog?.show()

        val titleTv = dialog!!.findViewById<TextView>(R.id.tv_dialog_title)
        val contentTv = dialog!!.findViewById<TextView>(R.id.tv_dialog_content)
        val cancelTv = dialog!!.findViewById<TextView>(R.id.tv_cancel)
        val tvOk = dialog!!.findViewById<TextView>(R.id.tv_ok)

        if (titleStr.isNotEmpty()) titleTv.text = titleStr
        titleTv.visibility =  if (titleStr.isNotEmpty()) View.VISIBLE else View.GONE

        if (contentStr.isNotEmpty()) contentTv.text = contentStr
        if (contentStrId!=0) contentTv.setText(contentStrId)
        if (cancelStr.isNotEmpty()) cancelTv.text = cancelStr
        if (okStr.isNotEmpty()) tvOk.text = okStr

        cancelTv.setOnClickListener {
            cancel()
            onDialogClickListener?.cancel()
        }
        tvOk.setOnClickListener {
            cancel()
            onDialogClickListener?.ok()
        }

        return this
    }

    fun show() {
        dialog?.show()
    }

    fun cancel() {
        dialog?.dismiss()
    }

    var onDialogClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun cancel()
        fun ok()
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        this.onDialogClickListener = onDialogClickListener
    }
}