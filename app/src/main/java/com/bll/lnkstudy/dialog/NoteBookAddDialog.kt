package com.bll.lnkstudy.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils


class NoteBookAddDialog(private val context: Context,private val screenPos:Int,val title: String,val name:String,val nameHint:String) {


    fun builder(): NoteBookAddDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_notebook_add, null)
        var dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context, 580f)
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,580f))/2
        }
        window.attributes = layoutParams

        val btn_ok = dialog?.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog?.findViewById<Button>(R.id.btn_cancel)

        val tvTitle=dialog?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.setText(title)

        val etName=dialog?.findViewById<EditText>(R.id.et_name)
        etName?.setText(name)
        etName?.hint=nameHint

        btn_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener {
            var nameStr=etName?.text.toString()
            if (!nameStr.isNullOrEmpty())
            {
                listener?.onClick(nameStr)
                dialog?.dismiss()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(string: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}