package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.utils.*


class NotebookDeletePasswordDialog(private val context: Context, private val screenPos:Int) {

    fun builder(): NotebookDeletePasswordDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_delete_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,460f))/2
        }
        dialog.show()

        val notePassword=SPUtil.getObj("notePassword",NotePassword::class.java)

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()

            if (passwordStr.isEmpty()){
                SToast.showText(screenPos,"输入密码")
                return@setOnClickListener
            }

            if (MD5Utils.digest(passwordStr)!=notePassword?.password){
                SToast.showText(screenPos,"密码输入错误")
                return@setOnClickListener
            }

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