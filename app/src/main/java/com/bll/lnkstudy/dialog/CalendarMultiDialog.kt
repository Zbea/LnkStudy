package com.bll.lnkstudy.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SToast
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import java.util.*


class CalendarMultiDialog(private val context: Context,private var times:MutableList<Long>,private var selectDateLong:MutableList<Long>) {

    private var dialog: Dialog? = null
    private lateinit var calendarView:CalendarView

    @SuppressLint("SetTextI18n")
    fun builder(): CalendarMultiDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_calendar_multi)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.BOTTOM or Gravity.LEFT
        layoutParams.y = DP2PX.dip2px(context, 320f)
        layoutParams.x = DP2PX.dip2px(context, 100f)
        dialog?.show()

        val tv_year = dialog?.findViewById<TextView>(R.id.tv_year)
        val iv_left = dialog?.findViewById<ImageView>(R.id.iv_left)
        val iv_right = dialog?.findViewById<ImageView>(R.id.iv_right)
        calendarView = dialog!!.findViewById(R.id.dp_date)

        initCalendar()

        tv_year?.text="${calendarView.curYear} 年  ${calendarView.curMonth} 月"

        iv_left?.setOnClickListener {
            calendarView.scrollToPre()
        }

        iv_right?.setOnClickListener {
            calendarView.scrollToNext()
        }

        calendarView.setOnMonthChangeListener { year, month ->
            tv_year?.text="$year 年  $month 月"
        }

         calendarView.setOnCalendarMultiSelectListener(object : CalendarView.OnCalendarMultiSelectListener {
            override fun onCalendarMultiSelectOutOfRange(calendar: Calendar?) {
            }
            override fun onMultiSelectOutOfSize(calendar: Calendar?, maxSize: Int) {
            }
            override fun onCalendarMultiSelect(calendar: Calendar?, curSize: Int, maxSize: Int) {
                val time = getCalenderTime(calendar!!)
                if (times.contains(time)){
                    times.remove(time)
                }
                else{
                    times.add(time)
                }
            }
        })

        calendarView.setOnCalendarInterceptListener(object : CalendarView.OnCalendarInterceptListener {
            override fun onCalendarIntercept(calendar: Calendar?): Boolean {
                val time=getCalenderTime(calendar!!)
                return selectDateLong.contains(time)
            }

            override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {
                SToast.showText(1,"不可再选当前日期")
            }
        })

        dialog?.setOnDismissListener {
            times.sort()
            dateListener?.getDate(times)
        }
        return this
    }

    private fun getCalenderTime(calendar: Calendar):Long{
        val years = calendar.year
        val months = calendar.month
        val days = calendar.day
        return DateUtils.dateToStamp(years,months,days)
    }

    private fun initCalendar(){
        for (time in times){
            val years=DateUtils.longToStringDataNoHour(time).split("-")
            val calendar=Calendar()
            calendar.year=years[0].toInt()
            calendar.month=years[1].toInt()
            calendar.day=years[2].toInt()
            calendarView.putMultiSelect(calendar)
        }
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(dates: MutableList<Long>)
    }

    fun setOnDateListener(dateListener: OnDateListener?) {
        this.dateListener = dateListener
    }


}