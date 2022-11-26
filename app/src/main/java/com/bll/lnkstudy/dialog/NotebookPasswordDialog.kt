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
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast


class NotebookPasswordDialog(private val context: Context, private val screenPos:Int) {

    fun builder(): NotebookPasswordDialog? {
        var dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_notebook_password)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        }
        window.attributes = layoutParams


        val btn_ok = dialog?.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog?.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog?.findViewById<EditText>(R.id.et_password)

        val tvFind = dialog?.findViewById<TextView>(R.id.tv_find_password)
        tvFind.setOnClickListener {
            dialog.dismiss()
            NotebookFindPasswordDialog(context,screenPos).builder()
        }

        val tvEdit = dialog?.findViewById<TextView>(R.id.tv_edit_password)
        tvEdit.setOnClickListener {
            dialog.dismiss()
            NotebookEditPasswordDialog(context,screenPos).builder()
        }

        btn_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            if (passwordStr.isNullOrEmpty()){
                SToast.showText(screenPos,"输入密码")
                return@setOnClickListener
            }
            val notePassword=SPUtil.getObj("notePassword",NotePassword::class.java)
            if (passwordStr != notePassword?.password){
                SToast.showText(screenPos,"输入密码错误")
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