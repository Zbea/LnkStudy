package com.bll.lnkstudy.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX

class ProgressNetworkDialog(var context: Context, screenPos: Int) {
    var mDialog: Dialog? = null
    var screenPos = 0 //当前屏幕位置

    init {
        this.screenPos = screenPos
        createDialog()
    }

    private fun createDialog() {
        mDialog = Dialog(context)
        mDialog!!.setContentView(R.layout.dialog_progress_network)
        val window = mDialog!!.window
        //要加上设置背景，否则dialog宽高设置无作用
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos == 2) {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        } else {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        }
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,120f))/2
        window.attributes = layoutParams
    }

    fun isShow():Boolean{
        return mDialog!!.isShowing
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