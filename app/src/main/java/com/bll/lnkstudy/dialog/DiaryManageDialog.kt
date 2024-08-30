package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.KeyboardUtils

class DiaryManageDialog(val context: Context,val type:Int) {
    var startLong=0L
    var endLong=0L

    fun builder(): DiaryManageDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_diary_upload)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =dialog.window?.attributes!!
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.text=if (type==1) "上传日记" else "删除日记"
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        et_name.visibility=if (type==1) View.VISIBLE else View.GONE
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tv_start_date = dialog.findViewById<TextView>(R.id.tv_start_date)
        val tv_end_date = dialog.findViewById<TextView>(R.id.tv_end_date)

        tv_start_date.setOnClickListener {
            CalendarSingleDialog(context,310f,480f).builder().setOnDateListener{
                startLong=it
                tv_start_date.text= DateUtils.longToStringDataNoYear(startLong)
            }
        }

        tv_end_date.setOnClickListener {
            CalendarSingleDialog(context,310f,480f).builder().setOnDateListener{
                endLong=it
                tv_end_date.text= DateUtils.longToStringDataNoYear(endLong)
            }
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            var titleStr=""
            if (type==1){
                titleStr=et_name.text.toString()
                if (titleStr.isEmpty()){
                    return@setOnClickListener
                }
            }
            if (startLong>0&&endLong>0&&startLong<endLong){
                dialog.dismiss()
                listener?.onClick(titleStr,startLong, endLong)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(name: String,startLong:Long,endLong: Long)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}