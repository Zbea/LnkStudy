package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SToast

class NumberDialog(val context: Context,val screenPos:Int,val string: String,val totalNum:Double=-1.0) {

    constructor(context: Context ,screenPos: Int,string: String) :this(context,screenPos,string,-1.0)
    constructor(context: Context ,string: String) :this(context,1,string,-1.0)

    fun builder(): NumberDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_score_number)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        }
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvName = dialog.findViewById<EditText>(R.id.ed_name)
        tvName?.hint=string

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val content = tvName?.text.toString()
            if (content.isNotEmpty()) {
                val score=content.toDouble()
                if (totalNum>0){
                    if (totalNum>=score){
                        dialog.dismiss()
                        listener?.onClick(score)
                    }
                    else{
                        SToast.showText(screenPos,"最大只能输入$totalNum")
                    }
                }
                else{
                    dialog.dismiss()
                    listener?.onClick(score)
                }
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(num:Double)
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener) {
        listener = onDialogClickListener
    }

}