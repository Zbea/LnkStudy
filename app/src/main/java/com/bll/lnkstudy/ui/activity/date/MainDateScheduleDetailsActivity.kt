package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DateTimeDialog
import com.bll.lnkstudy.dialog.RepeatDayDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.DateScheduleEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateRemind
import com.bll.lnkstudy.mvp.model.DateScheduleEvent
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.StringUtils
import kotlinx.android.synthetic.main.ac_mian_date_schedule_details.*
import kotlinx.android.synthetic.main.common_date_title.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class MainDateScheduleDetailsActivity: BaseActivity() {

    private var dateScheduleEvent: DateScheduleEvent?=null
    private var isEdit=false//是否是编辑状态
    private var endLong:Long=0//日程开始时间
    private var starLong:Long=0//日程结束时间
    private var startStr:String=""//日程开始小时分
    private var endStr:String=""//日程结束小时分
    private var nowTim= StringUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var repeatScheduleStr="不重复"
    private var remindscheduleAdapter: MainDateRemindAdapter?=null
    private var remindscheduleBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindscheduleTols= mutableListOf<DateRemind>()//全部提醒

    override fun layoutId(): Int {
        return R.layout.ac_mian_date_schedule_details
    }

    override fun initData() {
        dateScheduleEvent=intent.getBundleExtra("DATESCHEDULES")?.getSerializable("DATESCHEDULE") as DateScheduleEvent?
    }

    override fun initView() {
        setPageTitle("日程")

        starLong=dateScheduleEvent?.scheduleStartTime!!
        endLong=dateScheduleEvent?.scheduleEndTime!!
        startStr=dateScheduleEvent?.scheduleStartTimeStr!!
        endStr=dateScheduleEvent?.scheduleEndTimeStr!!
        nowTim=dateScheduleEvent?.scheduleDay!!
        //去空
        for ( item in dateScheduleEvent?.remindList!!)
        {
            if (item!=null)
                remindscheduleBeans.add(item)
        }

        et_schedule_title.setText(dateScheduleEvent?.scheduleTitle)
        tv_schedule_date_start.text=startStr
        tv_schedule_date_end.text=endStr

        remindscheduleTols= DataBeanManager.getIncetance().remindSchedule
        remindscheduleTols.removeAll(remindscheduleBeans)

        rv_schedule_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindscheduleAdapter = MainDateRemindAdapter(R.layout.item_date_remind, remindscheduleBeans)
        rv_schedule_remind.adapter = remindscheduleAdapter
        remindscheduleAdapter?.bindToRecyclerView(rv_schedule_remind)
        remindscheduleAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindscheduleTols.add(remindscheduleBeans[position])
                remindscheduleBeans.removeAt(position)
                remindscheduleAdapter?.notifyDataSetChanged()
            }
        }

        onClickEvent()
        contentShow()
    }

    //默认展示view
    private fun contentShow(){
        isEdit=false
        tv_edit_title.visibility= View.GONE
        tv_save.visibility=View.GONE
        tv_edit.visibility=View.VISIBLE
        tv_delete.visibility=View.VISIBLE

        et_schedule_title.isFocusable=false
        et_schedule_title.isFocusableInTouchMode=false
        ll_schedule_date_start.isClickable=false
        ll_schedule_date_end.isClickable=false
        ll_schedule_repeat.isClickable=false
        ll_schedule_remind.isClickable=false

        remindscheduleAdapter?.isShowClear(false)

    }

    //编辑展示view
    private fun contentShowEdit(){
        isEdit=true
        tv_edit_title.visibility= View.VISIBLE
        tv_save.visibility=View.VISIBLE
        tv_edit.visibility=View.GONE
        tv_delete.visibility=View.GONE

        et_schedule_title.isFocusable=true
        et_schedule_title.isFocusableInTouchMode=true
        et_schedule_title.requestFocus()

        ll_schedule_date_start.isClickable=true
        ll_schedule_date_end.isClickable=true
        ll_schedule_repeat.isClickable=true
        ll_schedule_remind.isClickable=true
        remindscheduleAdapter?.isShowClear(true)
    }

    private fun onClickEvent() {

        iv_back.setOnClickListener {
            if (isEdit){
                hideKeyboard()
                contentShow()
            }
            else{
                finish()
            }
        }
        tv_edit.setOnClickListener {
            hideKeyboard()
            if (isEdit){
               contentShow()
            }
            else{
                contentShowEdit()
            }
        }

        tv_delete.setOnClickListener {
            CommonDialog(this).setContent("确认删除日程？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    //删除系统提醒日历事件
                    CalendarReminderUtils.deleteCalendarEvent(this@MainDateScheduleDetailsActivity,dateScheduleEvent?.scheduleTitle)
                    DateScheduleEventGreenDaoManager.getInstance(this@MainDateScheduleDetailsActivity).deleteDateEvent(dateScheduleEvent)
                    EventBus.getDefault().post(DATE_EVENT)
                    finish()
                }

            })
        }

        ll_schedule_date_start.setOnClickListener {
            DateTimeDialog(this).builder().setDialogClickListener(object : DateTimeDialog.DateListener {
                override fun getDate(dateStr: String?,hourStr: String?, dateTim: Long) {
                    starLong=dateTim
                    startStr=hourStr!!
                    tv_schedule_date_start.text=hourStr
                    nowTim=dateTim
                }

            })
        }

        ll_schedule_date_end.setOnClickListener {
            DateTimeDialog(this).builder().setDialogClickListener(object : DateTimeDialog.DateListener {
                override fun getDate(dateStr: String?,hourStr: String?,dateTim: Long) {
                    endLong=dateTim
                    endStr=hourStr!!
                    tv_schedule_date_end.text=hourStr

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

        ll_schedule_remind.setOnClickListener {
            if (remindscheduleTols.size>0){
                remindscheduleBeans.add(remindscheduleTols[0])
                remindscheduleTols.removeAt(0)
                remindscheduleAdapter?.notifyDataSetChanged()
            }
        }

        tv_save.setOnClickListener {
            hideKeyboard()
            if (starLong > endLong) {
                showToast("请重新选择日程结束日期")
                return@setOnClickListener
            }
            var scheduleTitle=et_schedule_title.text.toString()
            if (TextUtils.isEmpty(scheduleTitle))
            {
                showToast("日程标题不能为空")
                return@setOnClickListener
            }
            CalendarReminderUtils.deleteCalendarEvent(this,dateScheduleEvent?.scheduleTitle)//删除原来的
            if (remindscheduleBeans.size>0||repeatScheduleStr!="不重复")
                CalendarReminderUtils.addCalendarEvent(this,scheduleTitle,"",starLong,endLong,remindscheduleBeans,repeatScheduleStr)

            dateScheduleEvent= DateScheduleEvent(dateScheduleEvent?.id, 1, scheduleTitle, nowTim, starLong, endLong, startStr, endStr,remindscheduleBeans,repeatScheduleStr)
            DateScheduleEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateScheduleEvent)
            EventBus.getDefault().post(DATE_EVENT)
            contentShow()
        }

    }

}