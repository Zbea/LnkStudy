package com.bll.lnkstudy.ui.activity.date

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopWindowDateSelector
import com.bll.lnkstudy.mvp.model.DateBean
import com.bll.lnkstudy.ui.adapter.DateAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.date.LunarSolarConverter
import com.bll.lnkstudy.utils.date.Solar
import kotlinx.android.synthetic.main.ac_date.*

class DateActivity:BaseAppCompatActivity() {

    private var yearPop:PopWindowDateSelector?=null
    private var monthPop:PopWindowDateSelector?=null
    private var yearNow=DateUtils.getYear()
    private var monthNow=DateUtils.getMonth()
    private var mAdapter:DateAdapter?=null
    private var dates= mutableListOf<DateBean>()

    override fun layoutId(): Int {
        return R.layout.ac_date
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle("日历")

        initRecycler()

        tv_year.text=yearNow.toString()
        tv_month.text=monthNow.toString()

        tv_year.setOnClickListener {
            val list= arrayListOf(2017,2018,2019,2020,2021,2022,2023,2024,2025,2026)
            if (yearPop==null){
                yearPop=PopWindowDateSelector(this,tv_year,list,0).builder()
                yearPop ?.setOnSelectorListener {
                    tv_year.text=it
                    yearNow=it.toInt()
                    getDates()
                }
                yearPop?.show()
            }
            else{
                yearPop?.show()
            }
        }

        tv_month.setOnClickListener {
            val list= mutableListOf<Int>()
            for (i in 1..12)
            {
                list.add(i)
            }
            if (monthPop==null){
                monthPop=PopWindowDateSelector(this,tv_month,list,1).builder()
                monthPop?.setOnSelectorListener {
                    tv_month.text=it
                    monthNow=it.toInt()
                    getDates()
                }
                monthPop?.show()
            }
            else{
                monthPop?.show()
            }
        }


    }

    private fun initRecycler(){
        mAdapter = DateAdapter(R.layout.item_date, null)
        rv_list.layoutManager = GridLayoutManager(this,7)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)

        getDates()
    }


    //根据月份获取当月日期
    fun getDates(){
        dates.clear()
        var lastYear: Int
        var lastMonth: Int
        var nextYear: Int
        var nextMonth: Int

        when (monthNow) {
            //当月为一月份时候
            1 -> {
                lastYear=yearNow-1
                lastMonth=12
                nextYear=yearNow
                nextMonth=monthNow+1
            }
            //当月为12月份时候
            12 -> {
                lastYear=yearNow
                lastMonth=monthNow-1
                nextYear=yearNow+1
                nextMonth=1
            }
            else -> {
                lastYear=yearNow
                lastMonth=monthNow-1
                nextYear=yearNow
                nextMonth=monthNow+1
            }
        }

        var week=DateUtils.getMonthOneDayWeek(yearNow,monthNow-1)
        if (week==1)
            week=8

        //补齐上月差数
        for (i in 0 until week-2){
//            //上月天数
//            val maxDay=DateUtils.getMonthMaxDay(lastYear,lastMonth-1)
//            val day=maxDay-(week-2)+(i+1)
//            dates.add(getDateBean(lastYear,lastMonth,day,false))
            dates.add(DateBean())
        }

        val max=DateUtils.getMonthMaxDay(yearNow,monthNow-1)
        for (i in 1 .. max)
        {
            dates.add(getDateBean(yearNow,monthNow,i,true))
        }

        if (dates.size>35){
            //补齐下月天数
            for (i in 0 until 42-dates.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dates.add(DateBean())
            }
        }
        else{
            for (i in 0 until 35-dates.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dates.add(DateBean())
            }
        }

        mAdapter?.setNewData(dates)

    }

    private fun getDateBean(year:Int,month:Int,day:Int,isMonth: Boolean):DateBean{
        val solar=Solar()
        solar.solarYear=year
        solar.solarMonth=month
        solar.solarDay=day

        val dateBean=DateBean()
        dateBean.year=year
        dateBean.month=month
        dateBean.day=day
        dateBean.time=DateUtils.dateToStamp("$year-$month-$day")
        dateBean.isNow=day==DateUtils.getDay()
        dateBean.isNowMonth=isMonth
        dateBean.solar= solar
        dateBean.week=DateUtils.getWeek(dateBean.time)
        dateBean.lunar=LunarSolarConverter.SolarToLunar(solar)

        return dateBean
    }


}