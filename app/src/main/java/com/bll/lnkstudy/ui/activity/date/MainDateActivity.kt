package com.bll.lnkstudy.ui.activity.date

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.ui.adapter.MainDateEventDayAdapter
import com.bll.lnkstudy.ui.adapter.MainDateEventPlanAdapter
import com.bll.lnkstudy.ui.adapter.MainDateEventScheduleAdapter
import com.bll.lnkstudy.utils.Lunar
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.haibin.calendarview.CalendarView
import kotlinx.android.synthetic.main.ac_mian_date.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


class MainDateActivity : BaseActivity() {

    private var dayLong= DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var planList= mutableListOf<DateEvent>()
    private var scheduleList= mutableListOf<DateEvent>()
    private var dayList= mutableListOf<DateEvent>()
    private var mainDateEventScheduleAdapter: MainDateEventScheduleAdapter?=null
    private var mainDateEventDayAdapter: MainDateEventDayAdapter?=null
    private var mainDateEventPlanAdapter: MainDateEventPlanAdapter?=null


    override fun layoutId(): Int {
        return R.layout.ac_mian_date
    }

    override fun initData() {
        findList(dayLong)
    }

    override fun initView() {

        EventBus.getDefault().register(this)

        tv_date.text=SimpleDateFormat("MM月dd日").format(Date())
        tv_year.text= DateUtils.getYear().toString()
        var dat= Lunar.getLunar(
            DateUtils.getYear(),
            DateUtils.getMonth(),
            DateUtils.getDay())
        tv_lunar.text= dat

        initRecyclerView()

        onClickEvent()

        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {

            override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
            }
            override fun onCalendarSelect(calendar: com.haibin.calendarview.Calendar?, isClick: Boolean) {
                val dayNow= DateUtils.dateToStamp("${calendar?.year}-"+"${calendar?.month}-"+"${calendar?.day}")
                dayLong=dayNow
                findList(dayNow)
            }

        })

    }


    //初始化recyclerview
    private fun initRecyclerView(){

        rv_plan.layoutManager = LinearLayoutManager(this)//创建布局管理
        mainDateEventPlanAdapter = MainDateEventPlanAdapter(R.layout.item_date_plan_event, planList)
        rv_plan.adapter = mainDateEventPlanAdapter
        mainDateEventPlanAdapter?.bindToRecyclerView(rv_plan)
        rv_plan.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mainDateEventPlanAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent= Intent(this, MainDatePlanDetailsActivity::class.java)
            val bundle= Bundle()
                bundle.putSerializable("DATEPLAN", planList[position])
            intent.putExtra("DATEPLANS", bundle)
            startActivity(intent)
        }

        rv_schedule.layoutManager = LinearLayoutManager(this)//创建布局管理
        mainDateEventScheduleAdapter = MainDateEventScheduleAdapter(R.layout.item_date_schedule_event, scheduleList)
        rv_schedule.adapter = mainDateEventScheduleAdapter
        mainDateEventScheduleAdapter?.bindToRecyclerView(rv_schedule)
        rv_schedule.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mainDateEventScheduleAdapter?.setOnItemClickListener { adapter, view, position ->

            val intent=Intent(this, MainDateScheduleDetailsActivity::class.java)
            val bundle=Bundle()
            bundle.putSerializable("DATESCHEDULE", scheduleList[position])
            intent.putExtra("DATESCHEDULES", bundle)
            startActivity(intent)

        }

        rv_day.layoutManager = LinearLayoutManager(this)//创建布局管理
        mainDateEventDayAdapter = MainDateEventDayAdapter(R.layout.item_date_day_event, dayList)
        rv_day.adapter = mainDateEventDayAdapter
        mainDateEventDayAdapter?.bindToRecyclerView(rv_day)
        rv_day.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mainDateEventDayAdapter?.setOnItemClickListener { adapter, view, position ->

            val intent=Intent(this, MainDateDayDetailsActivity::class.java)
            val bundle=Bundle()
            bundle.putSerializable("DATEDAY", dayList[position])
            intent.putExtra("DATEDAYS", bundle)
            startActivity(intent)

        }
    }


    private fun onClickEvent() {
        iv_add.setOnClickListener {
            startActivity(Intent(this, MainDateAddActivity::class.java))
        }
    }

    /**
     * 通过当天时间查找本地dateEvent事件集合
     */
    private fun findList(daylong:Long){
        planList = DateEventGreenDaoManager.getInstance(this).queryAllDateEvent(0,daylong)
        scheduleList = DateEventGreenDaoManager.getInstance(this).queryAllDateEvent(1,daylong)
        dayList = DateEventGreenDaoManager.getInstance(this).queryAllDateEvent(2,daylong)

        ll_plan_content.visibility=if (planList.size>0) View.VISIBLE else View.GONE
        ll_schedule_content.visibility=if (scheduleList.size>0) View.VISIBLE else View.GONE
        ll_day_content.visibility=if (dayList.size>0) View.VISIBLE else View.GONE

        mainDateEventScheduleAdapter?.setNewData(scheduleList)

        mainDateEventDayAdapter?.setDateLong(dayLong)
        mainDateEventDayAdapter?.setNewData(dayList)

        mainDateEventPlanAdapter?.setNewData(planList)
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==DATE_EVENT){
            findList(dayLong)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}