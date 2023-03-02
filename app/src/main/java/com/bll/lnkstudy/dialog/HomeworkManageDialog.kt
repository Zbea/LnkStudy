package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX

class HomeworkManageDialog(val context: Context, private val screenPos: Int,private var isCreate:Boolean) {

    fun builder(): HomeworkManageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_manage)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos == 3) {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x = (Constants.WIDTH - DP2PX.dip2px(context, 430F)) / 2
        }
        dialog.show()

        val iv_close = dialog.findViewById<ImageView>(R.id.iv_close)
        val ll_skin = dialog.findViewById<LinearLayout>(R.id.ll_skin)
        val ll_delete = dialog.findViewById<LinearLayout>(R.id.ll_delete)
        ll_delete.visibility=if (isCreate) View.VISIBLE else View.INVISIBLE

        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        ll_skin.setOnClickListener {
            onClickListener?.onSkin()
            dialog.dismiss()
        }

        ll_delete.setOnClickListener {
            onClickListener?.onDelete()
            dialog.dismiss()
        }

        return this
    }


    private var onClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onSkin()
        fun onDelete()
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }

}