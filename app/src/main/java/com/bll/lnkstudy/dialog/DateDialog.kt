package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by dell on 2017/10/23.
 */
class DateDialog(private val context: Context):NumberPicker.OnValueChangeListener {

    private var yearPicker: NumberPicker?=null
    private var monthPicker: NumberPicker?=null
    private var datePicker: NumberPicker?=null

    fun builder(): DateDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_date, null)
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
        yearPicker= view.findViewById(R.id.np_year)
        monthPicker= view.findViewById(R.id.np_month)
        datePicker= view.findViewById(R.id.np_date)

        yearPicker?.setOnValueChangedListener(this)
        monthPicker?.setOnValueChangedListener(this)

        //限制年份范围为前后五年
        val yearNow = calendar.get(Calendar.YEAR);
        yearPicker?.setMinValue(yearNow - 5);
        yearPicker?.setMaxValue(yearNow + 5);
        yearPicker?.setValue(yearNow);
        yearPicker?.setWrapSelectorWheel(false);  //关闭选择器循环

        //设置月份范围为1~12
        monthPicker?.setMinValue(1);
        monthPicker?.setMaxValue(12);
        monthPicker?.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker?.setWrapSelectorWheel(false);

        //日期限制存在变化，需要根据当月最大天数来调整
        datePicker?.setMinValue(1);
        datePicker?.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePicker?.setValue(calendar.get(Calendar.DATE));
        datePicker?.setWrapSelectorWheel(false);

        val dateTv = view.findViewById<TextView>(R.id.tv_date_today)
        dateTv.text = SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINA).format(Date())
        val cancleTv = view.findViewById<TextView>(R.id.tv_cancel)
        var okTv = view.findViewById<TextView>(R.id.tv_ok)
        val mDatePicker = view.findViewById<DatePicker>(R.id.dp_date)
        cancleTv.setOnClickListener { v: View? -> dialog.cancel() }
        okTv.setOnClickListener { v: View? ->
            dialog.cancel()
            val year = yearPicker?.value
            val month = monthPicker?.value
            val dayOfMonth = datePicker?.value
            val time = "$year 年$month 月$dayOfMonth 日"
            val dateToStamp = StringUtils.dateToStamp("$year-$month-$dayOfMonth")
            if (dateListener != null) dateListener!!.getDate(time, dateToStamp)
        }
        return this
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        val dateStr = java.lang.String.format(Locale.CHINA, "%d-%d", yearPicker?.getValue(), monthPicker?.getValue())
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.CHINA)
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(dateStr)
        val dateValue: Int = datePicker!!.value
        val maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        datePicker?.setMaxValue(maxValue)
        //重设日期值，防止月份变动时超过最大值
        //重设日期值，防止月份变动时超过最大值
        datePicker?.setValue(Math.min(dateValue, maxValue))
    }

    private var dateListener: DateListener? = null

    interface DateListener {
        fun getDate(dateStr: String?, dateTim: Long)
    }

    fun setDialogClickListener(dateListener: DateListener?) {
        this.dateListener = dateListener
    }


}