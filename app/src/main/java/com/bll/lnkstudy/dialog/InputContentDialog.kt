package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils

class InputContentDialog(val context: Context, private val screenPos:Int, val string: String) {


    fun builder(): InputContentDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_input_name)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        }
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val name = dialog.findViewById<EditText>(R.id.ed_name)
        name.hint=string


        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            var content = name.text.toString()
            if (!content.isNullOrEmpty()) {
                dialog.dismiss()
                listener?.onClick(content)
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(string: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}