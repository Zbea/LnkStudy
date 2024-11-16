package com.bll.lnkstudy.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.ToolUtils

class AccountEditPhoneDialog(val context: Context) {
    fun builder(): AccountEditPhoneDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_account_edit_phone)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val ed_phone = dialog.findViewById<EditText>(R.id.ed_phone)
        val ed_code = dialog.findViewById<EditText>(R.id.ed_code)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_code = dialog.findViewById<TextView>(R.id.btn_code)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val phone=ed_phone.text.toString()
            val code=ed_code.text.toString()
            if (phone.isNotEmpty()&&code.isNotEmpty())
            {
                if (ToolUtils.isPhoneNum(phone)){
                    dialog.dismiss()
                    listener?.onClick(code, phone)
                }
            }
        }
        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString()
            if (phone.isNotEmpty()){
                listener?.onPhone(phone)
                btn_code.isEnabled = false
                btn_code.isClickable = false
                object : CountDownTimer(60 * 1000, 1000) {
                    override fun onFinish() {
                        btn_code.isEnabled = true
                        btn_code.isClickable = true
                        btn_code.text = "获取验证码"
                    }
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        btn_code.text = "${millisUntilFinished / 1000}s"
                    }
                }.start()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(code: String,phone:String)
        fun onPhone(phone: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}