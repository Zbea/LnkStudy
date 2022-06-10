package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.DateTimeDialog
import com.bll.lnkstudy.dialog.DateTimeHourDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.DateDayEventGreenDaoManager
import com.bll.lnkstudy.manager.DatePlanEventGreenDaoManager
import com.bll.lnkstudy.manager.DateScheduleEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.ui.adapter.MainDateEventPlanAddAdapter
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.StringUtils
import com.bll.lnkstudy.dialog.RepeatDayDialog
import kotlinx.android.synthetic.main.ac_mian_date_add.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class MainDateAddActivity : BaseActivity() {

    private var endLong: Long = 0//日程开始时间
    private var starLong: Long = 0//日程结束时间
    private var scheduleDateStartStr: String = ""//日程开始小时分
    private var scheduleDateEndStr: String = ""//日程结束小时分
    private val nowTim = StringUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var type = 0//类型
    private var repeatScheduleStr="不重复"
    private var remindscheduleAdapter:MainDateRemindAdapter?=null
    private var remindscheduleBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindscheduleTols= mutableListOf<DateRemind>()//全部提醒


    private var dayLong: Long = 0//重要日子时间
    private var dayStr: String = ""//重要日子时间
    private var repeatDayStr="不重复"
    private var remindDayAdapter:MainDateRemindAdapter?=null
    private var remindDayBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindDayTols= mutableListOf<DateRemind>()//全部提醒

    private var endPlanLong: Long = 0
    private var startPlanLong: Long = 0
    private var endPlanStr: String = ""
    private var startPlanStr: String = ""
    private var repeatPlanStr="不重复"
    private var remindPlanAdapter: MainDateRemindAdapter?=null
    private var remindPlanBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindPlanTols= mutableListOf<DateRemind>()//全部提醒


    private var planList = mutableListOf<DatePlanBean>()
    private var adapterEventPlan: MainDateEventPlanAddAdapter? = null


    override fun layoutId(): Int {
        return R.layout.ac_mian_date_add
    }

    override fun initData() {

    }

    override fun initView() {
        initTab()
        initPlanEvent()
        initScheduleEvent()
        initDayEvent()
        onClickEvent()
    }

    //设置头部索引
    private fun initTab() {

        xt_date?.newTab()?.setText("学习计划")?.let { it -> xt_date?.addTab(it) }
        xt_date?.newTab()?.setText("日程")?.let { it -> xt_date?.addTab(it) }
        xt_date?.newTab()?.setText("重要日子")?.let { it -> xt_date?.addTab(it) }
        xt_date?.getTabAt(1)?.select()
        xt_date?.getTabAt(0)?.select()

        xt_date?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                if (tab?.text.toString() == "学习计划") {
                    type = 0
                    tv_title.text = "学习计划"
                    ll_project.visibility = View.VISIBLE
                    ll_schedule.visibility = View.GONE
                    ll_day.visibility = View.GONE

                } else if (tab?.text.toString() == "日程") {
                    type = 1
                    tv_title.text = "日程"
                    ll_project.visibility = View.GONE
                    ll_schedule.visibility = View.VISIBLE
                    ll_day.visibility = View.GONE

                } else {
                    type = 2
                    tv_title.text = "重要日子"
                    ll_project.visibility = View.GONE
                    ll_schedule.visibility = View.GONE
                    ll_day.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }

    /**
     * 设置学习计划添加
     */
    private fun initPlanEvent() {
        for (i in 0..7) {
            var date = DatePlanBean()
            date.id = i
            planList.add(date)
        }

        rv_plan_add.layoutManager = LinearLayoutManager(this)//创建布局管理
        adapterEventPlan = MainDateEventPlanAddAdapter(R.layout.item_main_date_plan_add, planList)
        rv_plan_add.adapter = adapterEventPlan
        adapterEventPlan?.bindToRecyclerView(rv_plan_add)

        tv_plan_add.setOnClickListener {
            var date = DatePlanBean()
            planList.add(date)
            adapterEventPlan?.notifyDataSetChanged()
        }

        ll_plan_start_date.setOnClickListener {
            DateTimeHourDialog(this).builder()
                .setDialogClickListener(object : DateTimeHourDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        startPlanLong = dateTim
                        startPlanStr = dateStr!!
                        tv_plan_start_day.text = dateStr
                    }
                })
        }
        ll_plan_end_date.setOnClickListener {
            DateTimeHourDialog(this).builder()
                .setDialogClickListener(object : DateTimeHourDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        endPlanLong = dateTim
                        endPlanStr = dateStr!!
                        tv_plan_end_date.text = dateStr
                    }
                })
        }
        ll_plan_repeat.setOnClickListener {
            RepeatDayDialog(this,repeatPlanStr,1).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatPlanStr=str!!
                    tv_plan_repeat.text=repeatPlanStr
                }
            })
        }

        remindPlanTols= DataBeanManager.getIncetance().remindSchedule

        rv_plan_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindPlanAdapter = MainDateRemindAdapter(R.layout.item_main_date_remind, null)
        rv_plan_remind.adapter = remindPlanAdapter
        remindPlanAdapter?.bindToRecyclerView(rv_plan_remind)
        remindPlanAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindPlanTols.add(remindPlanBeans[position])
                remindPlanBeans.removeAt(position)
                remindPlanAdapter?.setNewData(remindPlanBeans)
            }
        }

        ll_plan_remind.setOnClickListener {
            if (remindPlanTols.size>0){
                remindPlanBeans.add(remindPlanTols[0])
                remindPlanTols.removeAt(0)
                remindPlanAdapter?.setNewData(remindPlanBeans)
            }
        }

    }

    /**
     * 日程相关处理
     */
    private fun initScheduleEvent(){
        remindscheduleTols= DataBeanManager.getIncetance().remindSchedule

        rv_schedule_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindscheduleAdapter = MainDateRemindAdapter(R.layout.item_main_date_remind, null)
        rv_schedule_remind.adapter = remindscheduleAdapter
        remindscheduleAdapter?.bindToRecyclerView(rv_schedule_remind)
        remindscheduleAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindscheduleTols.add(remindscheduleBeans[position])
//                remindscheduleAdapter?.notifyItemRemoved(position)
                remindscheduleBeans.removeAt(position)
                remindscheduleAdapter?.setNewData(remindscheduleBeans)
            }
        }

        ll_schedule_remind.setOnClickListener {
            if (remindscheduleTols.size>0){
                remindscheduleBeans.add(remindscheduleTols[0])
                remindscheduleTols.removeAt(0)
                remindscheduleAdapter?.setNewData(remindscheduleBeans)
            }
        }

        ll_schedule_date_start.setOnClickListener {
            DateTimeDialog(this).builder()
                .setDialogClickListener(object : DateTimeDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        starLong = dateTim
                        scheduleDateStartStr = hourStr!!
                        tv_schedule_date_start.text = hourStr
                    }

                })
        }
        ll_schedule_date_end.setOnClickListener {
            DateTimeDialog(this).builder()
                .setDialogClickListener(object : DateTimeDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        endLong = dateTim
                        scheduleDateEndStr = hourStr!!
                        tv_schedule_date_end.text = hourStr

                    }

                })
        }
        ll_schedule_repeat.setOnClickListener {
            RepeatDayDialog(this,repeatScheduleStr,1).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatScheduleStr=str!!
                    tv_schedule_repeat.text=repeatScheduleStr
                }
            })
        }
    }

    /**
     * 重要日子相关处理
     */
    private fun initDayEvent(){
        remindDayTols= DataBeanManager.getIncetance().remindDay

        rv_day_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindDayAdapter = MainDateRemindAdapter(R.layout.item_main_date_remind, remindDayBeans)
        rv_day_remind.adapter = remindDayAdapter
        remindDayAdapter?.bindToRecyclerView(rv_day_remind)
        remindDayAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindDayTols.add(remindDayBeans[position])
                remindDayBeans.removeAt(position)
//                remindDayAdapter?.notifyItemRemoved(position)
                remindDayAdapter?.setNewData(remindDayBeans)
            }
        }
        ll_day_remind.setOnClickListener {
            if (remindDayTols.size>0){
                remindDayBeans.add(remindDayTols[0])
                remindDayTols.removeAt(0)
                remindDayAdapter?.setNewData(remindDayBeans)
            }
        }

        ll_day_repeat.setOnClickListener {
            RepeatDayDialog(this,repeatDayStr,0).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatDayStr=str!!
                    tv_day_repeat.text=repeatDayStr
                }
            })
        }
        tv_day_date.setOnClickListener {
            DateDialog(this).builder().setDialogClickListener(object : DateDialog.DateListener {
                override fun getDate(dateStr: String?, dateTim: Long) {
                    tv_day_date.text = dateStr
                    dayStr = dateStr!!
                    dayLong = dateTim
                }

            })
        }

    }


    private fun onClickEvent() {

        tv_save.setOnClickListener {
            hideKeyboard()
            //日程
            if (type == 1) {
                addSchedule()
            }
            //重要日子
            else if (type == 2) {
                addDay()
            }
            //学习计划
            else {
                addPlanE()
            }
        }

    }

    /**
     * 保存学习计划
     */
    private fun addPlanE(){
        if (TextUtils.isEmpty(startPlanStr)) {
            showToast("请选择学习计划开始时间")
            return
        }
        if (TextUtils.isEmpty(endPlanStr)) {
            showToast("请选择学习计划结束时间")
            return
        }
        if (startPlanLong > endPlanLong) {
            showToast("请重新选择学习计划结束时间")
            return
        }

        var dates = mutableListOf<DatePlanBean>()
        var items = adapterEventPlan?.data!!
        for (item in items) {
            if (!TextUtils.isEmpty(item.content) && !TextUtils.isEmpty(item.startTimeStr) && !TextUtils.isEmpty(
                    item.endTimeStr
                )
            ) {
                dates.add(item)
            }
        }
        if (dates.size > 0) {
            val datePlanEvent = DatePlanEvent(null, nowTim, startPlanLong, endPlanLong, startPlanStr, endPlanStr, dates,remindPlanBeans,repeatPlanStr)
            DatePlanEventGreenDaoManager.getInstance(this).insertOrReplaceDatePlanEvent(datePlanEvent)
            EventBus.getDefault().post(DATE_EVENT)
            if (remindPlanBeans.size>0||repeatPlanStr!="不重复")
                CalendarReminderUtils.addCalendarEvent(this,startPlanStr+"学习计划","",startPlanLong,endPlanLong,remindPlanBeans,repeatPlanStr)
            finish()
        } else {
            showToast("请输入学习计划")
        }
    }

    /**
     * 保存日程
     */
    private fun addSchedule(){
        var scheduleTitle = et_schedule_title.text.toString()
        if (TextUtils.isEmpty(scheduleTitle)) {
            showToast("请输入日程标题")
            return
        }
        if (TextUtils.isEmpty(tv_schedule_date_start.text.toString())) {
            showToast("请选择日程开始日期")
            return
        }
        if (TextUtils.isEmpty(tv_schedule_date_end.text.toString())) {
            showToast("请选择日程结束日期")
            return
        }

        if (starLong > endLong) {
            showToast("请重新选择日程结束日期")
            return
        }

        //获取选择开始日期当天的time
        val dayLong = StringUtils.dateToStamp(StringUtils.longToStringDataNoHour(starLong))

        val dateEvent = DateScheduleEvent(
            null,
            type,
            scheduleTitle,
            dayLong,
            starLong,
            endLong,
            scheduleDateStartStr,
            scheduleDateEndStr,
            remindscheduleBeans,
            repeatScheduleStr
        )
        if (remindscheduleBeans.size>0||repeatScheduleStr!="不重复")
            CalendarReminderUtils.addCalendarEvent(this,scheduleTitle,"",starLong,endLong,remindscheduleBeans,repeatScheduleStr)
        DateScheduleEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
        EventBus.getDefault().post(DATE_EVENT)
        finish()
    }

    /**
     * 保存重要日子
     */
    private fun addDay(){
        var dayTitle = et_day_title.text.toString()
        if (TextUtils.isEmpty(dayTitle)) {
//                    Toast.makeText(this,"请选择日期", Toast.LENGTH_SHORT).show()
            showToast("请输入重要日子")
            return
        }

        var dayExplain = et_explain.text.toString()

        if (TextUtils.isEmpty(tv_day_date.text.toString())) {
            showToast("请选择日期")
            return
        }
        val dayEvent = DateDayEvent(null, dayTitle, dayLong, dayStr, dayExplain, remindDayBeans,repeatDayStr)
        DateDayEventGreenDaoManager.getInstance(this).insertOrReplaceDateDayEvent(dayEvent)
        EventBus.getDefault().post(DATE_EVENT)
        if (remindDayBeans.size>0||repeatDayStr!="不重复")
            CalendarReminderUtils.addCalendarEvent2(this,dayTitle,dayExplain,dayLong,remindDayBeans,repeatDayStr)
        finish()
    }

}

