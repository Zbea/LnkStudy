package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.DateTimeDialog
import com.bll.lnkstudy.dialog.DateTimeHourDialog
import com.bll.lnkstudy.dialog.RepeatDayDialog
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.bll.lnkstudy.mvp.model.DateRemind
import com.bll.lnkstudy.ui.adapter.MainDateEventPlanAddAdapter
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_mian_date_add.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class MainDateAddActivity : BaseAppCompatActivity() {

    private val nowTim = DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var type = 0//类型

    private var endScheduleLong: Long = 0//日程开始时间
    private var startScheduleLong: Long = 0//日程结束时间
    private var scheduleDateStartStr: String = ""//日程开始小时分
    private var scheduleDateEndStr: String = ""//日程结束小时分
    private var repeatScheduleStr="不重复"
    private var remindScheduleAdapter:MainDateRemindAdapter?=null
    private var remindScheduleBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindScheduleAll= mutableListOf<DateRemind>()//全部提醒


    private var dayLong: Long = 0//重要日子时间
    private var dayStr: String = ""//重要日子时间
    private var repeatDayStr="不重复"
    private var remindDayAdapter:MainDateRemindAdapter?=null
    private var remindDayBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindDayAll= mutableListOf<DateRemind>()//全部提醒

    private var endPlanLong: Long = 0
    private var startPlanLong: Long = 0
    private var endPlanStr: String = ""
    private var startPlanStr: String = ""
    private var repeatPlanStr="不重复"
    private var remindPlanAdapter: MainDateRemindAdapter?=null
    private var remindPlanBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindPlanAll= mutableListOf<DateRemind>()//全部提醒


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
        adapterEventPlan = MainDateEventPlanAddAdapter(R.layout.item_date_plan_add, planList)
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

        remindPlanAll= DataBeanManager.getIncetance().remindSchedule

        rv_plan_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindPlanAdapter = MainDateRemindAdapter(R.layout.item_date_remind, null)
        rv_plan_remind.adapter = remindPlanAdapter
        remindPlanAdapter?.bindToRecyclerView(rv_plan_remind)
        remindPlanAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindPlanAll.add(remindPlanBeans[position])
                remindPlanBeans.removeAt(position)
                remindPlanAdapter?.setNewData(remindPlanBeans)
            }
        }

        ll_plan_remind.setOnClickListener {
            if (remindPlanAll.size>0){
                remindPlanBeans.add(remindPlanAll[0])
                remindPlanAll.removeAt(0)
                remindPlanAdapter?.setNewData(remindPlanBeans)
            }
        }

    }

    /**
     * 日程相关处理
     */
    private fun initScheduleEvent(){
        remindScheduleAll= DataBeanManager.getIncetance().remindSchedule

        rv_schedule_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindScheduleAdapter = MainDateRemindAdapter(R.layout.item_date_remind, null)
        rv_schedule_remind.adapter = remindScheduleAdapter
        remindScheduleAdapter?.bindToRecyclerView(rv_schedule_remind)
        remindScheduleAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindScheduleAll.add(remindScheduleBeans[position])
//                remindscheduleAdapter?.notifyItemRemoved(position)
                remindScheduleBeans.removeAt(position)
                remindScheduleAdapter?.setNewData(remindScheduleBeans)
            }
        }

        ll_schedule_remind.setOnClickListener {
            if (remindScheduleAll.size>0){
                remindScheduleBeans.add(remindScheduleAll[0])
                remindScheduleAll.removeAt(0)
                remindScheduleAdapter?.setNewData(remindScheduleBeans)
            }
        }

        ll_schedule_date_start.setOnClickListener {
            DateTimeDialog(this).builder()
                .setDialogClickListener(object : DateTimeDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        startScheduleLong = dateTim
                        scheduleDateStartStr = hourStr!!
                        tv_schedule_date_start.text = hourStr
                    }

                })
        }
        ll_schedule_date_end.setOnClickListener {
            DateTimeDialog(this).builder()
                .setDialogClickListener(object : DateTimeDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        endScheduleLong = dateTim
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
        remindDayAll= DataBeanManager.getIncetance().remindDay

        rv_day_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindDayAdapter = MainDateRemindAdapter(R.layout.item_date_remind, remindDayBeans)
        rv_day_remind.adapter = remindDayAdapter
        remindDayAdapter?.bindToRecyclerView(rv_day_remind)
        remindDayAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindDayAll.add(remindDayBeans[position])
                remindDayBeans.removeAt(position)
//                remindDayAdapter?.notifyItemRemoved(position)
                remindDayAdapter?.setNewData(remindDayBeans)
            }
        }
        ll_day_remind.setOnClickListener {
            if (remindDayAll.size>0){
                remindDayBeans.add(remindDayAll[0])
                remindDayAll.removeAt(0)
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

        var plans = mutableListOf<DatePlanBean>()
        var items = adapterEventPlan?.data!!
        for (item in items) {
            if (!TextUtils.isEmpty(item.content) && !TextUtils.isEmpty(item.startTimeStr) && !TextUtils.isEmpty(
                    item.endTimeStr
                )
            ) {
                plans.add(item)
            }
        }
        if (plans.size > 0) {
            val dateEvent = DateEvent()
            dateEvent.type=type
            dateEvent.dayLong=nowTim
            dateEvent.startTime=startPlanLong
            dateEvent.endTime=endPlanLong
            dateEvent.startTimeStr=startPlanStr
            dateEvent.endTimeStr=endPlanStr
            dateEvent.list=plans
            dateEvent.remindList=remindPlanBeans
            dateEvent.repeat=repeatPlanStr

            DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
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
        var title = et_schedule_title.text.toString()
        if (TextUtils.isEmpty(title)) {
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

        if (startScheduleLong > endScheduleLong) {
            showToast("请重新选择日程结束日期")
            return
        }

        //获取选择开始日期当天的time
        val dayLong = DateUtils.dateToStamp(
            DateUtils.longToStringDataNoHour(startScheduleLong))

        val dateEvent = DateEvent()
        dateEvent.type=type
        dateEvent.title=title
        dateEvent.dayLong=dayLong
        dateEvent.startTime=startScheduleLong
        dateEvent.endTime=endScheduleLong
        dateEvent.startTimeStr=scheduleDateStartStr
        dateEvent.endTimeStr=scheduleDateEndStr
        dateEvent.remindList=remindScheduleBeans
        dateEvent.repeat=repeatScheduleStr

        if (remindScheduleBeans.size>0||repeatScheduleStr!="不重复")
            CalendarReminderUtils.addCalendarEvent(this,title,"",startScheduleLong,endScheduleLong,remindScheduleBeans,repeatScheduleStr)
        DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
        EventBus.getDefault().post(DATE_EVENT)
        finish()
    }

    /**
     * 保存重要日子
     */
    private fun addDay(){
        var title = et_day_title.text.toString()
        if (TextUtils.isEmpty(title)) {
            showToast("请输入重要日子")
            return
        }

        var dayExplain = et_explain.text.toString()

        if (TextUtils.isEmpty(tv_day_date.text.toString())) {
            showToast("请选择日期")
            return
        }

        val dateEvent = DateEvent()
        dateEvent.type=type
        dateEvent.title=title
        dateEvent.dayLong=dayLong
        dateEvent.dayLongStr=dayStr
        dateEvent.remindList=remindDayBeans
        dateEvent.repeat=repeatDayStr
        dateEvent.explain=dayExplain

        DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
        EventBus.getDefault().post(DATE_EVENT)
        if (remindDayBeans.size>0||repeatDayStr!="不重复")
            CalendarReminderUtils.addCalendarEvent2(this,title,dayExplain,dayLong,remindDayBeans,repeatDayStr)
        finish()
    }

}

