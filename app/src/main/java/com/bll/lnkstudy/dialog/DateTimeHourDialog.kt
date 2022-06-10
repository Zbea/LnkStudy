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
class DateTimeHourDialog(private val context: Context) {

    private var hourPicker: NumberPicker?=null
    private var minutePicker: NumberPicker?=null

    fun builder(): DateTimeHourDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_date_hour, null)
        val dialog =
            AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = 800
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val calendar = Calendar.getInstance()
        hourPicker= view.findViewById(R.id.np_hour)
        minutePicker= view.findViewById(R.id.np_minute)

        val month= StringUtils.getMonth().toString()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val yearMonth=simpleDateFormat.format(Date())

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
            val hour = hourPicker?.value
            val munite = minutePicker?.value

            val time ="$hour 时$munite 分"
            val hourStr = "$yearMonth $hour-$munite"
            val dateToStamp = SimpleDateFormat("yyyy-MM-dd HH-mm").parse(hourStr).time

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