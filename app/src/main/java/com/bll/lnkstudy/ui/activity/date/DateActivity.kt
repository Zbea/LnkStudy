package com.bll.lnkstudy.ui.activity.date

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupDateSelector
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.ui.adapter.DateAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.date.LunarSolarConverter
import com.bll.lnkstudy.utils.date.Solar
import kotlinx.android.synthetic.main.ac_date.rv_list
import kotlinx.android.synthetic.main.common_title.ll_date
import kotlinx.android.synthetic.main.common_title.tv_month
import kotlinx.android.synthetic.main.common_title.tv_year

class DateActivity:BaseAppCompatActivity() {

    private var yearPop:PopupDateSelector?=null
    private var monthPop:PopupDateSelector?=null
    private var yearNow=DateUtils.getYear()
    private var monthNow=DateUtils.getMonth()
    private var mAdapter:DateAdapter?=null
    private var dateBeans= mutableListOf<DateBean>()
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_date
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle(R.string.date_title_str)
        showView(ll_date)

        initRecycler()

        tv_year.text=yearNow.toString()
        tv_month.text=monthNow.toString()

        tv_year.setOnClickListener {
            val list= arrayListOf(2018,2019,2020,2021,2022,2023,2024,2025,2026,2027)
            if (yearPop==null){
                yearPop=PopupDateSelector(this,tv_year,list,0).builder()
                yearPop ?.setOnSelectorListener {
                    tv_year.text=it
                    yearNow=it.toInt()
                    Thread{
                        getDates()
                    }.start()
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
                monthPop=PopupDateSelector(this,tv_month,list,1).builder()
                monthPop?.setOnSelectorListener {
                    tv_month.text=it
                    monthNow=it.toInt()
                    Thread {
                        getDates()
                    }.start()
                }
                monthPop?.show()
            }
            else{
                monthPop?.show()
            }
        }

        Thread {
            getDates()
        }.start()
    }

    private fun initRecycler(){
        mAdapter = DateAdapter(R.layout.item_date, null)
        rv_list.layoutManager = GridLayoutManager(this,7)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            val dateBean=dateBeans[position]
            if (dateBean.year!=0){
                val intent = Intent(this, DateEventActivity::class.java)
                intent.putExtra("date",dateBean.time)
                customStartActivity(intent)
            }
        }
    }


    //根据月份获取当月日期
    private fun getDates(){
        dateBeans.clear()
//        val lastYear: Int
//        val lastMonth: Int
//        val nextYear: Int
//        val nextMonth: Int
//
//        when (monthNow) {
//            //当月为一月份时候
//            1 -> {
//                lastYear=yearNow-1
//                lastMonth=12
//                nextYear=yearNow
//                nextMonth=monthNow+1
//            }
//            //当月为12月份时候
//            12 -> {
//                lastYear=yearNow
//                lastMonth=monthNow-1
//                nextYear=yearNow+1
//                nextMonth=1
//            }
//            else -> {
//                lastYear=yearNow
//                lastMonth=monthNow-1
//                nextYear=yearNow
//                nextMonth=monthNow+1
//            }
//        }

        var week=DateUtils.getMonthOneDayWeek(yearNow,monthNow-1)
        if (week==1)
            week=8

        //补齐上月差数
        for (i in 0 until week-2){
//            //上月天数
//            val maxDay=DateUtils.getMonthMaxDay(lastYear,lastMonth-1)
//            val day=maxDay-(week-2)+(i+1)
//            dates.add(getDateBean(lastYear,lastMonth,day,false))
            dateBeans.add(DateBean())
        }

        val max=DateUtils.getMonthMaxDay(yearNow,monthNow-1)
        for (i in 1 .. max)
        {
            dateBeans.add(getDateBean(yearNow,monthNow,i,true))
        }

        if (dateBeans.size>35){
            //补齐下月天数
            for (i in 0 until 42-dateBeans.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dateBeans.add(DateBean())
            }
        }
        else{
            for (i in 0 until 35-dateBeans.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dateBeans.add(DateBean())
            }
        }
        runOnUiThread {
            mAdapter?.setNewData(dateBeans)
        }
    }

    private fun getDateBean(year:Int,month:Int,day:Int,isMonth: Boolean): DateBean {
        val solar=Solar()
        solar.solarYear=year
        solar.solarMonth=month
        solar.solarDay=day

        val dateBean= DateBean()
        dateBean.year=year
        dateBean.month=month
        dateBean.day=day
        dateBean.time=DateUtils.dateToStamp("$year-$month-$day")
        dateBean.isNow=day==DateUtils.getDay()&&DateUtils.getMonth()==month
        dateBean.isNowMonth=isMonth
        dateBean.solar= solar
        dateBean.week=DateUtils.getWeek(dateBean.time)
        dateBean.lunar=LunarSolarConverter.SolarToLunar(solar)

        return dateBean
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag== Constants.DATE_DRAWING_EVENT){
            mAdapter?.notifyItemChanged(position)
        }
    }

}