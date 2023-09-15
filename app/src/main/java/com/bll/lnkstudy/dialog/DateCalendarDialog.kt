package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.CalendarView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils


class DateCalendarDialog(private val context: Context){

    private var dialog:Dialog?=null

    fun builder(): DateCalendarDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_calendar)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.TOP or  Gravity.END
        layoutParams.y= DP2PX.dip2px(context,175f)
        layoutParams.x=DP2PX.dip2px(context,50f)
        dialog?.show()

        val calendarView = dialog?.findViewById<CalendarView>(R.id.dp_date)
        calendarView?.firstDayOfWeek=2
        calendarView?.setOnDateChangeListener { calendarView, i, i2, i3 ->
            dismiss()
            val dateToStamp = "${i}-${i2+1}-${i3}"
            val time = DateUtils.dateToStamp(dateToStamp)
            dateListener?.getDate(time)
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
        fun getDate(dateTim: Long)
    }

    fun setOnDateListener(dateListener: OnDateListener?) {
        this.dateListener = dateListener
    }


}