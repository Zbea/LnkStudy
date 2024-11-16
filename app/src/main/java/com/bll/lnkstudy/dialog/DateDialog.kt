package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DateUtils


class DateDialog(private val context: Context,private val date:Long){

    private var dialog:Dialog?=null

    constructor(context: Context):this(context, 0L)

    fun builder(): DateDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_date)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val mDatePicker = dialog?.findViewById<DatePicker>(R.id.dp_date)
        if (date!=0L) {
            mDatePicker?.updateDate(DateUtils.getYear(date),DateUtils.getMonth(date)-1,DateUtils.getDay(date))
        }

        dialog?.setOnDismissListener {
            val year = mDatePicker?.year
            val month = mDatePicker?.month?.plus(1)
            val dayOfMonth = mDatePicker?.dayOfMonth
            val time = "${year}-${month}-${dayOfMonth}"
            dateListener?.getDate(time, DateUtils.dateToStamp(time))
        }
        return this
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(dateStr: String?, dateTim: Long)
    }

    fun setOnDateListener(dateListener: OnDateListener?) {
        this.dateListener = dateListener
    }


}