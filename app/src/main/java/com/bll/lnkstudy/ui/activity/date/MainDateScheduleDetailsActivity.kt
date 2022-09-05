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
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DateRemind
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_mian_date_schedule_details.*
import kotlinx.android.synthetic.main.common_date_title.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class MainDateScheduleDetailsActivity: BaseActivity() {

    private var dateScheduleEvent: DateEvent?=null
    private var isEdit=false//是否是编辑状态
    private var endLong:Long=0//日程开始时间
    private var starLong:Long=0//日程结束时间
    private var startStr:String=""//日程开始小时分
    private var endStr:String=""//日程结束小时分
    private var dayLong= DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var repeatStr="不重复"
    private var remindAdapter: MainDateRemindAdapter?=null
    private var remindBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindAlls= mutableListOf<DateRemind>()//全部提醒

    override fun layoutId(): Int {
        return R.layout.ac_mian_date_schedule_details
    }

    override fun initData() {
        dateScheduleEvent=intent.getBundleExtra("DATESCHEDULES")?.getSerializable("DATESCHEDULE") as DateEvent?
    }

    override fun initView() {
        setPageTitle("日程")

        starLong=dateScheduleEvent?.startTime!!
        endLong=dateScheduleEvent?.endTime!!
        startStr=dateScheduleEvent?.startTimeStr!!
        endStr=dateScheduleEvent?.endTimeStr!!
        dayLong=dateScheduleEvent?.dayLong!!
        //去空
        for ( item in dateScheduleEvent?.remindList!!)
        {
            if (item!=null)
                remindBeans.add(item)
        }

        et_schedule_title.setText(dateScheduleEvent?.title)
        tv_schedule_date_start.text=startStr
        tv_schedule_date_end.text=endStr

        remindAlls= DataBeanManager.getIncetance().remindSchedule
        remindAlls.removeAll(remindBeans)

        rv_schedule_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindAdapter = MainDateRemindAdapter(R.layout.item_date_remind, remindBeans)
        rv_schedule_remind.adapter = remindAdapter
        remindAdapter?.bindToRecyclerView(rv_schedule_remind)
        remindAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindAlls.add(remindBeans[position])
                remindBeans.removeAt(position)
                remindAdapter?.notifyDataSetChanged()
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

        remindAdapter?.isShowClear(false)

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
        remindAdapter?.isShowClear(true)
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
                    CalendarReminderUtils.deleteCalendarEvent(this@MainDateScheduleDetailsActivity,dateScheduleEvent?.title)
                    DateEventGreenDaoManager.getInstance(this@MainDateScheduleDetailsActivity).deleteDateEvent(dateScheduleEvent)
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
                    dayLong=dateTim
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
            RepeatDayDialog(this,repeatStr,1).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatStr=str!!
                    tv_schedule_repeat.text=repeatStr
                }
            })
        }

        ll_schedule_remind.setOnClickListener {
            if (remindAlls.size>0){
                remindBeans.add(remindAlls[0])
                remindAlls.removeAt(0)
                remindAdapter?.notifyDataSetChanged()
            }
        }

        tv_save.setOnClickListener {
            hideKeyboard()
            if (starLong > endLong) {
                showToast("请重新选择日程结束日期")
                return@setOnClickListener
            }
            var title=et_schedule_title.text.toString()
            if (TextUtils.isEmpty(title))
            {
                showToast("日程标题不能为空")
                return@setOnClickListener
            }
            CalendarReminderUtils.deleteCalendarEvent(this,dateScheduleEvent?.title)//删除原来的
            if (remindBeans.size>0||repeatStr!="不重复")
                CalendarReminderUtils.addCalendarEvent(this,title,"",starLong,endLong,remindBeans,repeatStr)

            val dateEvent = DateEvent()
            dateEvent.id=dateScheduleEvent?.id
            dateEvent.type=dateScheduleEvent?.type!!
            dateEvent.title=title
            dateEvent.dayLong=dayLong
            dateEvent.startTime=starLong
            dateEvent.endTime=endLong
            dateEvent.startTimeStr=startStr
            dateEvent.endTimeStr=endStr
            dateEvent.remindList=remindBeans
            dateEvent.repeat=repeatStr

            DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
            EventBus.getDefault().post(DATE_EVENT)
            contentShow()
        }

    }

}