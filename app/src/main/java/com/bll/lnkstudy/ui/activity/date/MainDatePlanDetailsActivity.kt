package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DateTimeHourDialog
import com.bll.lnkstudy.dialog.RepeatDayDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.bll.lnkstudy.mvp.model.DateRemind
import com.bll.lnkstudy.ui.adapter.MainDateAdapter
import com.bll.lnkstudy.ui.adapter.MainDateEventPlanAddAdapter
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.StringUtils
import kotlinx.android.synthetic.main.ac_mian_date_plan_details.*
import kotlinx.android.synthetic.main.common_date_title.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class MainDatePlanDetailsActivity: BaseActivity() {

    private var datePlanEvent: DateEvent?=null
    private var mainDateAdapter: MainDateAdapter?=null
    private var isEdit=false//是否是编辑状态
    private var adapterEventPlan: MainDateEventPlanAddAdapter?=null
    private var planList= mutableListOf<DatePlanBean>()
    private var dayLong= 0L //当天
    private var endPlanLong:Long=0
    private var startPlanLong:Long=0
    private var endPlanStr:String=""
    private var startPlanStr:String=""
    private var repeatStr="不重复"
    private var remindPlanAdapter: MainDateRemindAdapter?=null
    private var remindBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindAlls= mutableListOf<DateRemind>()//全部提醒

    override fun layoutId(): Int {
        return R.layout.ac_mian_date_plan_details
    }

    override fun initData() {
        datePlanEvent= intent.getBundleExtra("DATEPLANS")?.getSerializable("DATEPLAN") as DateEvent?
    }

    override fun initView() {
        setTitle("学习计划")

        startPlanStr=datePlanEvent?.startTimeStr!!
        endPlanStr=datePlanEvent?.endTimeStr!!
        startPlanLong=datePlanEvent?.startTime!!
        endPlanLong=datePlanEvent?.endTime!!
        dayLong=datePlanEvent?.dayLong!!
        //去空
        for ( item in datePlanEvent?.remindList!!)
        {
            if (item!=null)
                remindBeans.add(item)
        }
        repeatStr=datePlanEvent?.repeat!!
        remindAlls= DataBeanManager.getIncetance().remindSchedule
        remindAlls.removeAll(remindBeans)

        tv_plan_start.text=startPlanStr
        tv_plan_end.text=endPlanStr
        tv_plan_repeat.text=repeatStr

        rv_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindPlanAdapter = MainDateRemindAdapter(R.layout.item_date_remind, remindBeans)
        rv_remind.adapter = remindPlanAdapter
        remindPlanAdapter?.bindToRecyclerView(rv_remind)
        remindPlanAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindAlls.add(remindBeans[position])
                remindBeans.removeAt(position)
                remindPlanAdapter?.setNewData(remindBeans)
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
        tv_plan_add.visibility=View.GONE

        //不可点击
        ll_plan_start.isClickable=false
        ll_plan_end.isClickable=false
        ll_plan_remind.isClickable=false
        ll_plan_repeat.isClickable=false
        remindPlanAdapter?.isShowClear(false)

        rv_plan.layoutManager = LinearLayoutManager(this)//创建布局管理
        mainDateAdapter = MainDateAdapter(R.layout.item_date_plan_event_child, datePlanEvent?.list)
        rv_plan.adapter = mainDateAdapter
        mainDateAdapter?.bindToRecyclerView(rv_plan)



    }

    //修改展示view
    private fun contentShowEdit(){
        isEdit=true
        tv_edit_title.visibility= View.VISIBLE
        tv_save.visibility=View.VISIBLE
        tv_edit.visibility=View.GONE
        tv_delete.visibility=View.GONE
        tv_plan_add.visibility=View.VISIBLE

        //可点击
        ll_plan_start.isClickable=true
        ll_plan_end.isClickable=true
        ll_plan_remind.isClickable=true
        ll_plan_repeat.isClickable=true
        remindPlanAdapter?.isShowClear(true)

        planList.clear()
        planList.addAll(datePlanEvent?.list!!)
        if (planList.size<8){
            for (i in planList.size..7){
                planList.add(DatePlanBean())
            }
        }
        rv_plan.layoutManager = LinearLayoutManager(this)//创建布局管理
        adapterEventPlan = MainDateEventPlanAddAdapter(R.layout.item_date_plan_add, planList)
        rv_plan.adapter = adapterEventPlan
        adapterEventPlan?.bindToRecyclerView(rv_plan)

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

        tv_plan_add.setOnClickListener {
            planList.add(DatePlanBean())
            adapterEventPlan?.setNewData(planList)
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
            CommonDialog(this).setContent("确认删除学习计划？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    CalendarReminderUtils.deleteCalendarEvent(this@MainDatePlanDetailsActivity,datePlanEvent?.startTimeStr+"学习计划")
                    DateEventGreenDaoManager.getInstance(this@MainDatePlanDetailsActivity).deleteDateEvent(datePlanEvent)
                    EventBus.getDefault().post(DATE_EVENT)
                    finish()
                }

            })
        }

        ll_plan_repeat.setOnClickListener {
            RepeatDayDialog(this,repeatStr,1).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatStr=str!!
                    tv_plan_repeat.text=repeatStr
                }
            })
        }

        ll_plan_remind.setOnClickListener {
            if (remindAlls.size>0){
                remindBeans.add(remindAlls[0])
                remindAlls.removeAt(0)
                remindPlanAdapter?.setNewData(remindBeans)
            }
        }

        ll_plan_start.setOnClickListener {
            DateTimeHourDialog(this).builder().setDialogClickListener(object : DateTimeHourDialog.DateListener {
                override fun getDate(dateStr: String?,hourStr: String?, dateTim: Long) {
                    dayLong= StringUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
                    startPlanLong=dateTim
                    startPlanStr=dateStr!!
                    tv_plan_start.text=dateStr
                }

            })
        }

        ll_plan_end.setOnClickListener {
            DateTimeHourDialog(this).builder().setDialogClickListener(object : DateTimeHourDialog.DateListener {
                override fun getDate(dateStr: String?,hourStr: String?,dateTim: Long) {
                    endPlanLong=dateTim
                    endPlanStr=dateStr!!
                    tv_plan_end.text=dateStr

                }

            })
        }

        tv_save.setOnClickListener {
            hideKeyboard()
            if (startPlanLong > endPlanLong) {
                showToast("请重新选择学习计划结束时间")
                return@setOnClickListener
            }

            var plans= mutableListOf<DatePlanBean>()
            var items= adapterEventPlan?.data!!
            for (item in items){
                if (!TextUtils.isEmpty(item.content)&&!TextUtils.isEmpty(item.startTimeStr)&&!TextUtils.isEmpty(item.endTimeStr))
                {
                    plans.add(item)
                }
            }
            if (plans.size>0){

                CalendarReminderUtils.deleteCalendarEvent(this,datePlanEvent?.startTimeStr+"学习计划")//删除原来的
                if (remindBeans.size>0||repeatStr!="不重复")
                    CalendarReminderUtils.addCalendarEvent(this,startPlanStr+"学习计划","",startPlanLong,endPlanLong,remindBeans,repeatStr)

                val dateEvent = DateEvent()
                dateEvent.id=datePlanEvent?.id
                dateEvent.type=datePlanEvent?.type!!
                dateEvent.dayLong=dayLong
                dateEvent.startTime=startPlanLong
                dateEvent.endTime=endPlanLong
                dateEvent.startTimeStr=startPlanStr
                dateEvent.endTimeStr=endPlanStr
                dateEvent.list=plans
                dateEvent.remindList=remindBeans
                dateEvent.repeat=repeatStr

                DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
                EventBus.getDefault().post(DATE_EVENT)
                contentShow()
            }
        }

    }



}