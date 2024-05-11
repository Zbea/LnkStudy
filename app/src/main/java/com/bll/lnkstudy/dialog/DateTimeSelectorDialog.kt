package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import android.widget.TimePicker
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*


class DateTimeSelectorDialog(private val context: Context, private val item: DatePlan, private val type:Int) {
    private var dialog:Dialog?=null

    fun builder(): DateTimeSelectorDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_date_time_selector)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val yearMonth=simpleDateFormat.format(Date())

        val tp_start_time = dialog?.findViewById<TimePicker>(R.id.tp_start_time)
        tp_start_time?.setIs24HourView(true)
        if (!item.startTimeStr.isNullOrEmpty()){
            tp_start_time?.hour=item.startTimeStr.split(":")[0].toInt()
            tp_start_time?.minute=item.startTimeStr.split(":")[1].toInt()
        }

        val tp_end_time = dialog?.findViewById<TimePicker>(R.id.tp_end_time)
        tp_end_time?.setIs24HourView(true)
        if (!item.endTimeStr.isNullOrEmpty()){
            tp_end_time?.hour=item.endTimeStr.split(":")[0].toInt()
            tp_end_time?.minute=item.endTimeStr.split(":")[1].toInt()
        }

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {

            val startHour=tp_start_time?.hour
            val startMinute=tp_start_time?.minute

            val startStr="${getFormat(startHour!!)}:${getFormat(startMinute!!)}"
            val startLong=DateUtils.date3Stamp("$yearMonth $startStr")

            val endHour=tp_end_time?.hour
            val endMinute=tp_end_time?.minute

            val endStr="${getFormat(endHour!!)}:${getFormat(endMinute!!)}"
            val endLong=DateUtils.date3Stamp("$yearMonth $endStr")

            if (endLong>startLong){
                dateListener?.getDate(startStr,endStr)
                dismiss()
            }
        }
        return this
    }

    /**
     * 格式化时间
     */
    private fun getFormat(num:Int):String{
        return if (num<10) "0$num" else "$num"
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(startStr: String,endStr: String)
    }

    fun setOnDateListener(dateListener:OnDateListener) {
        this.dateListener = dateListener
    }


}