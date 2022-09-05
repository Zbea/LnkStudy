package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TimePicker
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils

/**
 * 课程表 时间选择
 */
class CourseTimeDialog(val context: Context){
    private var isStart=true

    fun builder(): CourseTimeDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_course_time, null)
        val dialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context,450F)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams

        var rg=dialog.findViewById<RadioGroup>(R.id.rg_course)
        rg.setOnCheckedChangeListener { radioGroup, i ->
            isStart = i==R.id.rb_start
        }
        val rbStart=dialog.findViewById<RadioButton>(R.id.rb_start)
        rbStart.text= DateUtils.longToHour1(System.currentTimeMillis())
        val rbEnd=dialog.findViewById<RadioButton>(R.id.rb_end)

        val timePicker=dialog.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener { timePicker, i, i2 ->
            var s= "$i:$i2"
            if (isStart)
            {
                rbStart.text=s
            }
            else{
                rbEnd.text=s
            }
        }

        val cancleTv = view.findViewById<Button>(R.id.btn_cancel)
        cancleTv.setOnClickListener { v: View? -> dialog.dismiss()}
        var okTv = view.findViewById<Button>(R.id.btn_ok)
        okTv.setOnClickListener {
            var timeStr=rbStart.text.toString()+"~"+rbEnd.text.toString()
            if (!rbStart.text.toString().isNullOrEmpty()&&!rbEnd.text.toString().isNullOrEmpty())
            {
                if (onClickListener!=null)
                    onClickListener?.onSelect(timeStr)
                dialog.dismiss()
            }
        }

        return this
    }


    private var onClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onSelect(course:String)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}