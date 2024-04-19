package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.SToast
import com.google.gson.Gson


class PrivacyPasswordFindDialog(private val context: Context,private val type:Int=0) {

    fun builder(): PrivacyPasswordFindDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_privacy_password_find)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val privacyPassword=MethodManager.getPrivacyPassword(type)

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog.findViewById<EditText>(R.id.et_password_again)
        val etPasswordFind=dialog.findViewById<EditText>(R.id.et_question_password)
        val tvFind=dialog.findViewById<TextView>(R.id.tv_question_password)
        tvFind.text=privacyPassword?.question


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val passwordFindStr=etPasswordFind?.text.toString()

            if (passwordFindStr.isEmpty()){
                SToast.showText(2,R.string.toast_password_input_question)
                return@setOnClickListener
            }

            if (passwordFindStr!=privacyPassword?.answer){
                SToast.showText(2,R.string.toast_password_question_error)
                return@setOnClickListener
            }

            if (passwordStr.isEmpty()){
                SToast.showText(2,R.string.login_input_password_hint)
                return@setOnClickListener
            }
            if (passwordAgainStr.isEmpty()){
                SToast.showText(2,R.string.password_again_error)
                return@setOnClickListener
            }

            if (passwordStr!=passwordAgainStr){
                SToast.showText(2,R.string.password_different)
                return@setOnClickListener
            }

            privacyPassword.password=MD5Utils.digest(passwordStr)
            MethodManager.savePrivacyPassword(type,privacyPassword)
            //更新增量更新
            DataUpdateManager.editDataUpdate(10,type,1,1, Gson().toJson(privacyPassword))
            dialog.dismiss()
            listener?.onClick()

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