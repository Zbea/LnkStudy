package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.KeyboardUtils

class InputContentDialog(val context: Context,val string: String) {


    fun builder(): InputContentDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_account_edit_name)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val name = dialog.findViewById<EditText>(R.id.ed_name)
        name.hint=string
        dialog.show()

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

    interface OnDialogClickListener {
        fun onClick(string: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}