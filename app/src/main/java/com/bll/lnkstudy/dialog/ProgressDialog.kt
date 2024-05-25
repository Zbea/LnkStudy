package com.bll.lnkstudy.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX

class ProgressDialog(var context: Context, val screenPos: Int,val type:Int) {
    var mDialog: Dialog? = null

    init {
        createDialog()
    }

    private fun createDialog() {
        mDialog = Dialog(context)
        mDialog!!.setContentView(R.layout.dialog_progress)
//        if (type!=1)
//            mDialog!!.setCanceledOnTouchOutside(false)
        val window = mDialog!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos == 1) {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        } else {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,120f))/2
        window.attributes = layoutParams

        val tvName=mDialog?.findViewById<TextView>(R.id.tv_name)
        tvName?.text=if (type==0) "加载中..." else "网络连接中..."
    }

    fun setCanceledOutside(boolean: Boolean){
        mDialog?.setCanceledOnTouchOutside(boolean)
    }

    fun show() {
        val activity = context as Activity
        if (!activity.isFinishing && !activity.isDestroyed && mDialog != null && !mDialog!!.isShowing) {
            mDialog!!.show()
        }
    }

    fun dismiss() {
        val activity = context as Activity
        if (!activity.isFinishing && !activity.isDestroyed && mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
    }
}