package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils


class NotebookAddDialog(private val context: Context, private val screenPos:Int, val title: String, val name:String, val nameHint:String) {


    fun builder(): NotebookAddDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_add)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        }
        dialog.show()

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