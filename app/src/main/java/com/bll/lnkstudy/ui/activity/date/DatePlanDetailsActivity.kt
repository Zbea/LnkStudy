package com.bll.lnkstudy.ui.activity.date

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateSelectorDialog
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.ui.adapter.DatePlanEventAddAdapter
import com.bll.lnkstudy.ui.adapter.DatePlanWeekAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import kotlinx.android.synthetic.main.ac_date_plan_details.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class DatePlanDetailsActivity:BaseAppCompatActivity() {

    private var flags=0
    private var planList = mutableListOf<DatePlan>()
    private var mAdapter: DatePlanEventAddAdapter? = null
    private var mWeekAdapter: DatePlanWeekAdapter? = null
    private var dateEventBean: DateEventBean?=null
    private var oldEvent: DateEventBean?=null
    private var weeks= mutableListOf<DateWeek>()
    private var dateDialog:DateSelectorDialog?=null
    private var startStr=""
    private var endStr=""

    init {
        startStr=getString(R.string.start)
        endStr=getString(R.string.end)
    }

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_details
    }

    override fun initData() {
        flags=intent.flags
        weeks= DataBeanManager.weeks

        if (flags==0){
            dateEventBean= DateEventBean()
            dateEventBean?.type=0

            for (i in 0..7) {
                val date = DatePlan()
                planList.add(date)
            }
        }
        else{
            dateEventBean = intent.getBundleExtra("bundle")?.getSerializable("dateEvent") as DateEventBean
            oldEvent=dateEventBean?.clone() as DateEventBean
            et_title.setText(dateEventBean?.title)
            tv_start_date.text=dateEventBean?.startTimeStr
            tv_end_date.text=dateEventBean?.endTimeStr

            for (item in weeks){
                for (ite in dateEventBean?.weeks!!){
                    if (ite.week==item.week)
                        item.isCheck=ite.isCheck
                }
            }
            planList=dateEventBean?.plans!!
            if (dateEventBean?.plans?.size!! <8) {
                for (i in 0 until  8-dateEventBean?.plans?.size!!){
                    val date = DatePlan()
                    planList.add(date)
                }
            }

        }


    }

    override fun initView() {

        setPageTitle(R.string.date_plan)
        setPageSetting(R.string.save)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanEventAddAdapter(R.layout.item_date_plan_add, planList)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)

        rv_week.layoutManager = GridLayoutManager(this,7) //创建布局管理
        mWeekAdapter = DatePlanWeekAdapter(R.layout.item_date_plan_add_week, weeks)
        rv_week.adapter = mWeekAdapter
        mWeekAdapter?.bindToRecyclerView(rv_week)

        iv_add.setOnClickListener {
            val date = DatePlan()
            planList.add(date)
            mAdapter?.notifyDataSetChanged()
        }

        rl_date.setOnClickListener{
            if (dateDialog==null){
                dateDialog=DateSelectorDialog(this,screenPos).builder()
                dateDialog?.setOnDateListener { startStr, startLong, endStr, endLong ->

                    dateEventBean?.startTime=startLong
                    dateEventBean?.startTimeStr=startStr
                    tv_start_date?.text=startStr
                    dateEventBean?.dayLong=endLong
                    dateEventBean?.endTime=endLong
                    dateEventBean?.endTimeStr=endStr
                    tv_end_date?.text=endStr
                }
            }
            else{
                dateDialog?.show()
            }
        }

        tv_setting.setOnClickListener {
            save()
        }

    }

    private fun save(){

        val titleStr = et_title.text.toString()
        if (titleStr.isEmpty()) {
            showToast(R.string.toast_input_title)
            return
        }
        dateEventBean?.title=titleStr

        val selectWeeks= mutableListOf<DateWeek>()

        for (item in mWeekAdapter?.data!!){
            if (item.isCheck)
                selectWeeks.add(item)
        }

        if (selectWeeks.size==0){
            showToast(R.string.toast_select_week)
            return
        }

        if (dateEventBean?.endTimeStr!!.isNullOrEmpty()){
            showToast(R.string.toast_select_date)
            return
        }

        dateEventBean?.weeks=selectWeeks

        var plans = mutableListOf<DatePlan>()
        var items = mAdapter?.data!!
        for (item in items) {
            if (!item.content.isNullOrEmpty() && !item.course.isNullOrEmpty() && !item.endTimeStr.isNullOrEmpty()) {
                plans.add(item)
            }
        }

        dateEventBean?.plans=plans

        DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEventBean)

        //删除原来的日历
        if (oldEvent!=null){
            for (item in oldEvent?.plans!!){
                if (item.isRemindStart){
                    CalendarReminderUtils.deleteCalendarEvent(this,oldEvent?.title+"${startStr}："+item.course+item.content)
                }
                if (item.isRemindEnd){
                    CalendarReminderUtils.deleteCalendarEvent(this,oldEvent?.title+"${endStr}："+item.course+item.content)
                }
            }
        }

        for (item in plans){
            if (item.isRemindStart){
                CalendarReminderUtils.addCalendarEvent(this,
                    dateEventBean?.title+"${startStr}："+item.course+item.content,
                    item.startTimeStr,
                    dateEventBean?.startTime!!,
                    dateEventBean?.endTime!!,
                selectWeeks)
            }
            if (item.isRemindEnd){
                CalendarReminderUtils.addCalendarEvent(this,
                    dateEventBean?.title+"${endStr}："+item.course+item.content,
                    item.endTimeStr,
                    dateEventBean?.startTime!!,
                    dateEventBean?.endTime!!,
                    selectWeeks)
            }
        }

        EventBus.getDefault().post(Constants.DATE_EVENT)
        finish()
    }



}