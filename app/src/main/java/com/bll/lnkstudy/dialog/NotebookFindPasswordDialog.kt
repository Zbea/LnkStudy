package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson


class NotebookFindPasswordDialog(private val context: Context, private val screenPos:Int) {

    fun builder(): NotebookFindPasswordDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_find_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        }
        dialog.show()

        val user=SPUtil.getObj("user", User::class.java)
        val notePassword=SPUtil.getObj("${user?.accountId}notePassword",NotePassword::class.java)

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog.findViewById<EditText>(R.id.et_password_again)
        val etPasswordFind=dialog.findViewById<EditText>(R.id.et_question_password)
        val tvFind=dialog.findViewById<TextView>(R.id.tv_question_password)
        tvFind.text=notePassword?.question


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val passwordFindStr=etPasswordFind?.text.toString()

            if (passwordFindStr.isEmpty()){
                SToast.showText(screenPos,R.string.toast_password_input_question)
                return@setOnClickListener
            }

            if (passwordFindStr!=notePassword?.answer){
                SToast.showText(screenPos,R.string.toast_password_question_error)
                return@setOnClickListener
            }

            if (passwordStr.isEmpty()){
                SToast.showText(screenPos,R.string.login_input_password_hint)
                return@setOnClickListener
            }
            if (passwordAgainStr.isEmpty()){
                SToast.showText(screenPos,R.string.password_again_error)
                return@setOnClickListener
            }

            if (passwordStr!=passwordAgainStr){
                SToast.showText(screenPos,R.string.password_different)
                return@setOnClickListener
            }

            notePassword.password=MD5Utils.digest(passwordStr)
            SPUtil.putObj("notePassword",notePassword)
            //更新增量更新
            DataUpdateManager.editDataUpdate(10,1,1,1, Gson().toJson(notePassword))
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