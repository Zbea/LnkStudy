package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.SToast


class PrivacyPasswordDialog(private val context: Context,private val type:Int=0) {

    fun builder(): PrivacyPasswordDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_privacy_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val tvFind = dialog.findViewById<TextView>(R.id.tv_find_password)

        tvFind.setOnClickListener {
            dialog.dismiss()
            PrivacyPasswordFindDialog(context,type).builder()
        }

        val tvEdit = dialog.findViewById<TextView>(R.id.tv_edit_password)
        tvEdit.setOnClickListener {
            dialog.dismiss()
            PrivacyPasswordEditDialog(context,type).builder()
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            if (passwordStr.isEmpty()){
                SToast.showText(2,R.string.login_input_password_hint)
                return@setOnClickListener
            }
            val privacyPassword=MethodManager.getPrivacyPassword(type)
            if (MD5Utils.digest(passwordStr) != privacyPassword?.password){
                SToast.showText(2,R.string.toast_password_error)
                return@setOnClickListener
            }
            listener?.onClick()
            dialog.dismiss()

        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}