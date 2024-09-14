package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.KeyboardUtils

class AccountEditParentDialog(val context: Context, val name: String, val nickname: String, val phone: String) {

    fun builder(): AccountEditParentDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_account_edit_parent)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val nameTv = dialog.findViewById<EditText>(R.id.ed_name)
        nameTv.setText(name)
        nameTv.setSelection(name.length)
        val nicknameTv = dialog.findViewById<EditText>(R.id.ed_nickname)
        nicknameTv.setText(nickname)
        nicknameTv.setSelection(nickname.length)
        val phoneTv = dialog.findViewById<EditText>(R.id.ed_phone)
        phoneTv.setText(phone)
        phoneTv.setSelection(phone.length)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val nameStr = nameTv.text.toString()
            val nicknameStr = nicknameTv.text.toString()
            val phoneStr = phoneTv.text.toString()
            if (nameStr.isNotEmpty()&&nicknameStr.isNotEmpty()&&phoneStr.isNotEmpty()) {
                dialog.dismiss()
                listener?.onClick(nameStr,nicknameStr,phoneStr)
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(name: String,nickname: String,phone: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}