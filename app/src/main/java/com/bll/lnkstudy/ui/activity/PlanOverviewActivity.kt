package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_plan_overview.*
import java.text.DecimalFormat

class PlanOverviewActivity:BaseDrawingActivity() {

    private val weekTime=7*24*60*60*1000
    private var type=1//1月计划 2 周计划
    private var nowYear=0
    private var nowMonth=1
    private var weekStartDate=0L
    private var weekEndDate=0L

    override fun layoutId(): Int {
        return R.layout.ac_plan_overview
    }

    override fun initData() {
    }
    override fun initView() {
        setPageTitle(R.string.main_plan)

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            type = if (i==R.id.rb_month){
                1
            } else{
                2
            }
            setChangeDate()
        }

        nowYear=DateUtils.getYear()
        nowMonth=DateUtils.getMonth()

        weekStartDate=DateUtils.getCurrentWeekTimeFrame()[0]
        weekEndDate=DateUtils.getCurrentWeekTimeFrame()[1]
        setChangeDate()

        iv_up.setOnClickListener {
            if (type==1){
                if (nowMonth==1){
                    nowMonth=12
                    nowYear-=1
                }
                else{
                    nowMonth-=1
                }
            }
            else{
                weekStartDate-=weekTime
                weekEndDate-=weekTime
            }
            setChangeDate()
        }

        iv_down.setOnClickListener {
            if (type==1){
                if (nowMonth==12){
                    nowMonth=1
                    nowYear+=1
                }
                else{
                    nowMonth+=1
                }
            }
            else{
                weekStartDate+=weekTime
                weekEndDate+=weekTime
            }
            setChangeDate()
        }

    }

    private fun setChangeDate(){
        if (type==1){
            tv_date.text=nowYear.toString()+"年"+DecimalFormat("00").format(nowMonth)+"月"
        }
        else{
            tv_date.text=DateUtils.longToStringDataNoYear(weekStartDate)+"~"+DateUtils.longToStringDataNoYear(weekEndDate)
        }
    }

}