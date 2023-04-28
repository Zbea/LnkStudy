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


class NotebookAddDiaryDialog(private val context: Context, private val screenPos:Int) {

    private var dateDialog:DateSelectorDialog?=null

    fun builder(): NotebookAddDiaryDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_add_diary)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        }
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etName=dialog.findViewById<EditText>(R.id.et_name)
        val tvDate=dialog.findViewById<TextView>(R.id.tv_date)
        tvDate.setOnClickListener {
            if (dateDialog==null){
                dateDialog=DateSelectorDialog(context,screenPos).builder()
                dateDialog?.setOnDateListener { startStr, startLong, endStr, endLong ->
                    tvDate.text= "$startStr ~ $endStr"
                }
            }
            else{
                dateDialog?.show()
            }
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val nameStr=etName?.text.toString()
            val dateStr=tvDate?.text.toString()
            if (nameStr.isNotEmpty() && dateStr.isNotEmpty())
            {
                listener?.onClick(nameStr,dateStr)
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(name: String,dateString: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}