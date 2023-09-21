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
import com.bll.lnkstudy.mvp.model.CheckPassword
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson


class NotebookSetPasswordDialog(private val context: Context) {

    private val popWindowBeans= mutableListOf<PopupBean>()

    fun builder(): NotebookSetPasswordDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_set_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        popWindowBeans.add(
            PopupBean(
                0,
                context.getString(R.string.password_father_name),
                false
            )
        )
        popWindowBeans.add(
            PopupBean(
                1,
                context.getString(R.string.password_mother_name),
                false
            )
        )
        popWindowBeans.add(
            PopupBean(
                2,
                context.getString(R.string.password_grandfather_name),
                false
            )
        )
        popWindowBeans.add(
            PopupBean(
                3,
                context.getString(R.string.password_grandmother_name),
                false
            )
        )

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog.findViewById<EditText>(R.id.et_password_again)
        val etPasswordQuestion=dialog.findViewById<EditText>(R.id.et_question_password)
        val tvQuestion=dialog.findViewById<TextView>(R.id.tv_question_password)
        tvQuestion.setOnClickListener {
            PopupList(context, popWindowBeans, tvQuestion, 5).builder()
            .setOnSelectListener { item ->
                tvQuestion.text = item.name
            }
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val answerStr=etPasswordQuestion?.text.toString()
            val questionStr=tvQuestion?.text.toString()
            if (questionStr==context.getString(R.string.password_question_select_str)){
                SToast.showText(1,R.string.password_question_select_str)
                return@setOnClickListener
            }
            if (answerStr.isEmpty()){
                SToast.showText(1,R.string.toast_password_input_question)
                return@setOnClickListener
            }

            if (passwordStr.isEmpty()){
                SToast.showText(1,R.string.password_input)
                return@setOnClickListener
            }
            if (passwordAgainStr.isEmpty()){
                SToast.showText(1,R.string.password_again)
                return@setOnClickListener
            }

            if (passwordStr!=passwordAgainStr){
                SToast.showText(1,R.string.password_different)
                return@setOnClickListener
            }
            val checkPassword= CheckPassword()
            checkPassword.question=tvQuestion.text.toString()
            checkPassword.answer=answerStr
            checkPassword.password=MD5Utils.digest(passwordStr)
            val user=SPUtil.getObj("user", User::class.java)
            SPUtil.putObj("${user?.accountId}notePassword",checkPassword)
            //创建增量数据(日记密码)
            DataUpdateManager.createDataUpdate(10,1,1,1, Gson().toJson(checkPassword))
            dialog.dismiss()
            listener?.onClick(checkPassword)

        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(checkPassword: CheckPassword)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}