package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DateUtils


class DateSelectorDialog(private val context: Context) {

    private var dialog:Dialog?=null

    fun builder(): DateSelectorDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_date_selector)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val dp_start = dialog?.findViewById<DatePicker>(R.id.dp_start)
        ((dp_start?.getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0).visibility=View.GONE

        val dp_end = dialog?.findViewById<DatePicker>(R.id.dp_end)
        ((dp_end?.getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0).visibility=View.GONE

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        var okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {

            val startYear = dp_start?.year
            val startMonth = dp_start?.month?.plus(1)
            val startDay = dp_start?.dayOfMonth

            val startStr = "${startMonth}月${startDay}日"
            val startLong = DateUtils.dateToStamp(startYear!!,startMonth!!,startDay!!)

            val endYear = dp_end?.year
            val endMonth = dp_end?.month?.plus(1)
            val endDay = dp_end?.dayOfMonth

            val endStr = "${endMonth}月${endDay}日"
            val endLong = DateUtils.dateToStamp(endYear!!,endMonth!!,endDay!!)

            if (endLong>=startLong){
                dateListener?.getDate(startStr,startLong,endStr,endLong)
                dismiss()
            }

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
        fun getDate(startStr: String?, startLong: Long?,endStr: String?, endLong: Long?)
    }

    fun setOnDateListener(dateListener:OnDateListener) {
        this.dateListener = dateListener
    }

    private fun disYear(v:DatePicker) {
        val fields= v.javaClass.declaredFields
        try {
            for (field in fields){
                if(field.name.equals("mYearPicker") || field.name.equals("mYearSpinner"))
                {
                    //解封装
                    field.isAccessible=true
                    //获取当前实例的值
                    val yearView= field.get(v) as View
                    yearView.visibility=View.GONE
                }
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }


}