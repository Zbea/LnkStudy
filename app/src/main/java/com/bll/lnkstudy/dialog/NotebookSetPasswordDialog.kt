package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast


class NotebookSetPasswordDialog(private val context: Context, private val screenPos:Int) {

    private val popWindowBeans= mutableListOf<PopWindowBean>()

    fun builder(): NotebookSetPasswordDialog? {
        var dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_notebook_set_password)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        }
        window.attributes = layoutParams

        popWindowBeans.add(PopWindowBean(0,"爸爸姓名？",false))
        popWindowBeans.add(PopWindowBean(1,"妈妈姓名？",false))
        popWindowBeans.add(PopWindowBean(2,"爷爷姓名？",false))
        popWindowBeans.add(PopWindowBean(3,"奶奶姓名？",false))

        val btn_ok = dialog?.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog?.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog?.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog?.findViewById<EditText>(R.id.et_password_again)
        val etPasswordQuestion=dialog?.findViewById<EditText>(R.id.et_question_password)
        val tvQuestion=dialog?.findViewById<TextView>(R.id.tv_question_password)
        tvQuestion.setOnClickListener {
            PopWindowList(context, popWindowBeans, tvQuestion, 5).builder()
            ?.setOnSelectListener { item ->
                tvQuestion.text = item.name
            }
        }

        btn_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val answerStr=etPasswordQuestion?.text.toString()
            val questionStr=tvQuestion?.text.toString()
            if (questionStr=="选择问题"){
                SToast.showText(screenPos,"选择密码")
                return@setOnClickListener
            }
            if (answerStr.isNullOrEmpty()){
                SToast.showText(screenPos,"输入密保答案")
                return@setOnClickListener
            }

            if (passwordStr.isNullOrEmpty()){
                SToast.showText(screenPos,"输入密码")
                return@setOnClickListener
            }
            if (passwordAgainStr.isNullOrEmpty()){
                SToast.showText(screenPos,"再次输入密码")
                return@setOnClickListener
            }

            if (passwordStr!=passwordAgainStr){
                SToast.showText(screenPos,"密码输入不一致")
                return@setOnClickListener
            }
            val notePassword=NotePassword()
            notePassword.question=tvQuestion.text.toString()
            notePassword.answer=answerStr
            notePassword.password=passwordStr
            SPUtil.putObj("notePassword",notePassword)
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