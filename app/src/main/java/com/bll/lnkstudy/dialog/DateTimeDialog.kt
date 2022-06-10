package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.NumberPicker
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by dell on 2017/10/23.
 */
class DateTimeDialog(private val context: Context) {

    private var datePicker: NumberPicker?=null
    private var hourPicker: NumberPicker?=null
    private var minutePicker: NumberPicker?=null

    fun builder(): DateTimeDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_date_time, null)
        val dialog =
            AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width =800
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val calendar = Calendar.getInstance()
        hourPicker= view.findViewById(R.id.np_hour)
        minutePicker= view.findViewById(R.id.np_minute)
        datePicker= view.findViewById(R.id.np_date)

        var monthTv = view.findViewById<TextView>(R.id.tv_date)
        val month= StringUtils.getMonth().toString()
        monthTv.text=month+"月"

        calendar[Calendar.DATE] = 1 //把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1) //日期回滚一天，也就是最后一天
        val maxDate = calendar[Calendar.DATE]

        val simpleDateFormat = SimpleDateFormat("yyyy-MM")
        val yearMonth=simpleDateFormat.format(Date())

        datePicker?.minValue=1
        datePicker?.maxValue=maxDate
        datePicker?.value=StringUtils.getDay()
        datePicker?.wrapSelectorWheel=false

        //24小时制，限制小时数为0~23
        hourPicker?.minValue=0
        hourPicker?.maxValue=23
        hourPicker?.value=calendar.get(Calendar.HOUR_OF_DAY)
        hourPicker?.wrapSelectorWheel=false

        //限制分钟数为0~59
        minutePicker?.minValue=0
        minutePicker?.maxValue=59
        minutePicker?.value=calendar.get(Calendar.MINUTE)
        minutePicker?.wrapSelectorWheel=false


        val dateTv = view.findViewById<TextView>(R.id.tv_date_today)
        dateTv.text = SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())
        val cancleTv = view.findViewById<TextView>(R.id.tv_cancel)
        var okTv = view.findViewById<TextView>(R.id.tv_ok)

        cancleTv.setOnClickListener { v: View? -> dialog.cancel() }
        okTv.setOnClickListener { v: View? ->
            dialog.cancel()
            val date = datePicker?.value
            val hour = hourPicker?.value
            val munite = minutePicker?.value

            val time =month+"月"+"$date 日$hour 时$munite 分"
            val hourStr ="$date 日$hour 时$munite 分"
            val dateToStamp = SimpleDateFormat("yyyy-MM-dd HH-mm").parse("$yearMonth-$date $hour-$munite").time

             dateListener?.getDate(time,hourStr,dateToStamp)
        }
        return this
    }

    private var dateListener: DateListener? = null

    interface DateListener {
        fun getDate(dateStr: String?, hourStr: String?,dateTim: Long)
    }

    fun setDialogClickListener(dateListener: DateListener?) {
        this.dateListener = dateListener
    }


}