package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DatePlan
import com.bll.lnkstudy.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*


class DateTimeSelectorDialog(private val context: Context, private val item: DatePlan, private val type:Int) {
    private var dialog:Dialog?=null
    private var isRemindStart=false
    private var isRemindEnd=false

    fun builder(): DateTimeSelectorDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_date_time_selector)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        isRemindStart=item.isRemindStart
        isRemindEnd=item.isRemindEnd


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

        val ll_remind_start = dialog?.findViewById<LinearLayout>(R.id.ll_remind_start)
        ll_remind_start?.visibility= if (type==0) View.GONE else View.VISIBLE
        val ll_remind_end = dialog?.findViewById<LinearLayout>(R.id.ll_remind_end)
        ll_remind_end?.visibility=  if (type==0) View.GONE else View.VISIBLE

        val ll_bell_start = dialog?.findViewById<LinearLayout>(R.id.ll_bell_start)
        ll_bell_start?.visibility= if (isRemindStart) View.VISIBLE else View.INVISIBLE
        val ll_bell_end = dialog?.findViewById<LinearLayout>(R.id.ll_bell_end)
        ll_bell_end?.visibility= if (isRemindEnd) View.VISIBLE else View.INVISIBLE


        val sh_remind_start = dialog?.findViewById<Switch>(R.id.sh_remind_start)
        sh_remind_start?.isChecked=isRemindStart
        sh_remind_start?.setOnClickListener {
            isRemindStart=!isRemindStart
            ll_bell_start?.visibility= if (isRemindStart) View.VISIBLE else View.INVISIBLE
        }
        val sh_remind_end = dialog?.findViewById<Switch>(R.id.sh_remind_end)
        sh_remind_end?.isChecked=isRemindEnd
        sh_remind_end?.setOnClickListener {
            isRemindEnd=!isRemindEnd
            ll_bell_end?.visibility= if (isRemindEnd) View.VISIBLE else View.INVISIBLE
        }

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        var okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

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
                dateListener?.getDate(startStr,endStr,isRemindStart,isRemindEnd)
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
        fun getDate(startStr: String?,endStr: String?,isRemindStart:Boolean,isRemindEnd:Boolean)
    }

    fun setOnDateListener(dateListener:OnDateListener) {
        this.dateListener = dateListener
    }


}