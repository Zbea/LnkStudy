package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.KeyboardUtils


class ClassGroupAddDialog(private val context: Context) {


    fun builder(): ClassGroupAddDialog? {
        var dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_classgroup_add)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val btn_ok = dialog?.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog?.findViewById<Button>(R.id.btn_cancel)

        val etNumber=dialog?.findViewById<EditText>(R.id.et_number)

        btn_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener {
            var number=etNumber?.text.toString()
            if (!number.isNullOrEmpty())
            {
                listener?.onClick(number.toInt())
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
        fun onClick(code: Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}