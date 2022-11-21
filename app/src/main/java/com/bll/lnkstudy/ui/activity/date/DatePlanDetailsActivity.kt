package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateSelectorDialog
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.bll.lnkstudy.mvp.model.DateWeekBean
import com.bll.lnkstudy.ui.adapter.DatePlanEventAddAdapter
import com.bll.lnkstudy.ui.adapter.DatePlanWeekAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import kotlinx.android.synthetic.main.ac_date_plan_details.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class DatePlanDetailsActivity:BaseAppCompatActivity() {

    private var flags=0
    private var planList = mutableListOf<DatePlanBean>()
    private var mAdapter: DatePlanEventAddAdapter? = null
    private var mWeekAdapter: DatePlanWeekAdapter? = null
    private var dateEvent:DateEvent?=null
    private var weeks= mutableListOf<DateWeekBean>()
    private var dateDialog:DateSelectorDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_details
    }

    override fun initData() {
        flags=intent.flags
        weeks= DataBeanManager.getIncetance().weeks

        if (flags==0){
            dateEvent= DateEvent()
            dateEvent?.type=0

            for (i in 0..7) {
                var date = DatePlanBean()
                planList.add(date)
            }
        }
        else{
            dateEvent = intent.getBundleExtra("bundle").getSerializable("dateEvent") as DateEvent
            et_title.setText(dateEvent?.title)
            tv_start_date.text=dateEvent?.startTimeStr
            tv_end_date.text=dateEvent?.endTimeStr

            for (item in weeks){
                for (ite in dateEvent?.weeks!!){
                    if (ite.week==item.week)
                        item.isCheck=ite.isCheck
                }
            }
            planList=dateEvent?.plans!!
            if (dateEvent?.plans?.size!! <8) {
                for (i in 0 until  8-dateEvent?.plans?.size!!){
                    var date = DatePlanBean()
                    planList.add(date)
                }
            }

        }


    }

    override fun initView() {

        setPageTitle("学习计划")
        setPageSetting("保存")

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanEventAddAdapter(R.layout.item_date_plan_add, planList)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)

        rv_week.layoutManager = GridLayoutManager(this,7) //创建布局管理
        mWeekAdapter = DatePlanWeekAdapter(R.layout.item_date_plan_add_week, weeks)
        rv_week.adapter = mWeekAdapter
        mWeekAdapter?.bindToRecyclerView(rv_week)

        iv_add.setOnClickListener {
            var date = DatePlanBean()
            planList.add(date)
            mAdapter?.notifyDataSetChanged()
        }

        rl_date.setOnClickListener{
            if (dateDialog==null){
                dateDialog=DateSelectorDialog(this).builder()
                dateDialog?.setOnDateListener { startStr, startLong, endStr, endLong ->

                    dateEvent?.startTime=startLong
                    dateEvent?.startTimeStr=startStr
                    tv_start_date?.text=startStr
                    dateEvent?.dayLong=endLong
                    dateEvent?.endTime=endLong
                    dateEvent?.endTimeStr=endStr
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
        if (titleStr.isNullOrEmpty()) {
            showToast("请输入标题")
            return
        }
        dateEvent?.title=titleStr

        val selectWeeks= mutableListOf<DateWeekBean>()

        for (item in mWeekAdapter?.data!!){
            if (item.isCheck)
                selectWeeks.add(item)
        }

        if (selectWeeks.size==0){
            showToast("请选择星期")
            return
        }

        dateEvent?.weeks=selectWeeks

        var plans = mutableListOf<DatePlanBean>()
        var items = mAdapter?.data!!
        for (item in items) {
            if (!TextUtils.isEmpty(item.content) && !TextUtils.isEmpty(item.course) && !TextUtils.isEmpty(item.endTimeStr)) {
                plans.add(item)
            }
        }

        dateEvent?.plans=plans

        DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEvent)

        for (item in plans){
            if (item.isRemindStart){
                CalendarReminderUtils.addCalendarEvent(this,
                    "开始："+item.content,
                    item.startTimeStr,
                    dateEvent?.startTime!!,
                    dateEvent?.endTime!!,
                selectWeeks)
            }
            if (item.isRemindEnd){
                CalendarReminderUtils.addCalendarEvent(this,
                    "结束："+item.content,
                    item.endTimeStr,
                    dateEvent?.startTime!!,
                    dateEvent?.endTime!!,
                    selectWeeks)
            }
        }

        EventBus.getDefault().post(Constants.DATE_EVENT)
        finish()
    }



}