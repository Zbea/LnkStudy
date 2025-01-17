package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.KeyboardUtils


class ClassGroupAddDialog(private val context: Context) {

    private var tvInfo:TextView?=null

    fun builder(): ClassGroupAddDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_add)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvInfo = dialog.findViewById(R.id.tv_info)

        val etNumber=dialog.findViewById<EditText>(R.id.et_number)
        etNumber.doAfterTextChanged {
            if (it.toString().isNotEmpty()){
                listener?.onEditTextCode(it.toString().toInt())
            }
            else{
                setTextInfo("")
            }
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val number=etNumber?.text.toString()
            if (number.isNotEmpty())
            {
                listener?.onClick(number.toInt())
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    fun setTextInfo(string: String){
        tvInfo?.text=string
    }

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(code: Int)
        fun onEditTextCode(code: Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}