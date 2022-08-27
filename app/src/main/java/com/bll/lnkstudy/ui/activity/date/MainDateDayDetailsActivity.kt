package com.bll.lnkstudy.ui.activity.date

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.RepeatDayDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DateRemind
import com.bll.lnkstudy.ui.adapter.MainDateRemindAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import kotlinx.android.synthetic.main.ac_mian_date_day_details.*
import kotlinx.android.synthetic.main.common_date_title.*
import org.greenrobot.eventbus.EventBus

class MainDateDayDetailsActivity: BaseActivity() {

    private var dateEvent: DateEvent?=null
    private var isEdit=false//是否是编辑状态
    private var dayLong:Long=0//重要日子时间
    private var dayStr:String=""//重要日子时间
    private var repeatDayStr="不重复"
    private var remindDayAdapter: MainDateRemindAdapter?=null
    private var remindBeans= mutableListOf<DateRemind>()//已经添加提醒
    private var remindAlls= mutableListOf<DateRemind>()//全部提醒


    override fun layoutId(): Int {
        return R.layout.ac_mian_date_day_details
    }

    override fun initData() {
        dateEvent=intent.getBundleExtra("DATEDAYS")?.getSerializable("DATEDAY") as DateEvent?
    }

    override fun initView() {

        setPageTitle("重要日子")

        remindAlls= DataBeanManager.getIncetance().remindDay
        dayLong=dateEvent?.dayLong!!
        dayStr=dateEvent?.dayLongStr!!
        repeatDayStr=dateEvent?.repeat!!
        //去空
        for ( item in dateEvent?.remindList!!)
        {
            if (item!=null)
                remindBeans.add(item)
        }
        //全部提醒去除已添加的提醒
        remindAlls.removeAll(remindBeans)

        et_day_title.setText(dateEvent?.title)
        tv_day_date.text=dayStr
        if (TextUtils.isEmpty(dateEvent?.explain))
        {
            et_explain.hint="说明"
        }
        else{
            et_explain.setText(dateEvent?.explain)
        }
        tv_day_repeat.text=repeatDayStr

        rv_day_remind.layoutManager = LinearLayoutManager(this)//创建布局管理
        remindDayAdapter = MainDateRemindAdapter(R.layout.item_date_remind, remindBeans)
        rv_day_remind.adapter = remindDayAdapter
        remindDayAdapter?.bindToRecyclerView(rv_day_remind)
        remindDayAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_clear){
                remindAlls.add(remindBeans[position])
                remindBeans.removeAt(position)
                remindDayAdapter?.notifyDataSetChanged()
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

        //编辑标题不可编辑
        et_day_title.isFocusable=false
        et_day_title.isFocusableInTouchMode=false
        //说明不可编辑
        et_explain.isFocusable=false
        et_explain.isFocusableInTouchMode=false
        //日期选择不可点击
        ll_day.isClickable=false
        ll_day_repeat.isClickable=false
        ll_day_remind.isClickable=false

        remindDayAdapter?.isShowClear(false)

    }

    //修改展示view
    private fun contentShowEdit(){
        isEdit=true
        tv_edit_title.visibility= View.VISIBLE
        tv_save.visibility=View.VISIBLE
        tv_edit.visibility=View.GONE
        tv_delete.visibility=View.GONE

        et_day_title.isFocusable=true
        et_day_title.isFocusableInTouchMode=true
        et_day_title.requestFocus()

        et_explain.isFocusable=true
        et_explain.isFocusableInTouchMode=true

        ll_day.isClickable=true
        ll_day_repeat.isClickable=true
        ll_day_remind.isClickable=true

        remindDayAdapter?.isShowClear(true)

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
        //删除
        tv_delete.setOnClickListener {
            CommonDialog(this).setContent("确认删除重要日子？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    //删除添加的日历事件
                    CalendarReminderUtils.deleteCalendarEvent(this@MainDateDayDetailsActivity,dateEvent?.title)
                    DateEventGreenDaoManager.getInstance(this@MainDateDayDetailsActivity).deleteDateEvent(dateEvent)
                    EventBus.getDefault().post(DATE_EVENT)
                    finish()
                }

            })
        }

        //选择日期
        ll_day.setOnClickListener {

            DateDialog(this).builder().setDialogClickListener(object : DateDialog.DateListener {
                override fun getDate(dateStr: String?, dateTim: Long) {
                    tv_day_date.text=dateStr
                    dayStr=dateStr!!
                    dayLong=dateTim
                }

            })

        }

        //添加提醒处理
        ll_day_remind.setOnClickListener {
            if (remindAlls.size>0){
                remindBeans.add(remindAlls[0])
                remindAlls.removeAt(0)
                remindDayAdapter?.notifyDataSetChanged()
            }
        }

        //点击重复处理
        ll_day_repeat.setOnClickListener {
            RepeatDayDialog(this,repeatDayStr,0).builder().setDialogClickListener(object :
                RepeatDayDialog.OnRepeatListener {
                override fun getRepeat(str: String?) {
                    repeatDayStr=str!!
                    tv_day_repeat.text=repeatDayStr
                }
            })
        }

        tv_save.setOnClickListener {
            hideKeyboard()
            var dayTitle=et_day_title.text.toString()
            if (TextUtils.isEmpty(dayTitle))
            {
                showToast("重要日子标题不能为空")
                return@setOnClickListener
            }
            var dayExplain=et_explain.text.toString()

            val dateDayEvent = DateEvent()
            dateDayEvent.id=dateEvent?.id
            dateDayEvent.type= dateEvent?.type!!
            dateDayEvent.title=dayTitle
            dateDayEvent.dayLong=dayLong
            dateDayEvent.dayLongStr=dayStr
            dateDayEvent.remindList=remindBeans
            dateDayEvent.repeat=repeatDayStr
            dateDayEvent.explain=dayExplain

            DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateDayEvent)
            EventBus.getDefault().post(DATE_EVENT)

            CalendarReminderUtils.deleteCalendarEvent(this,dateEvent?.title)//删除原来的
            if (remindBeans.size>0||repeatDayStr!="不重复"){
                CalendarReminderUtils.addCalendarEvent2(this,dayTitle,dayExplain,dayLong,remindBeans,repeatDayStr)
            }
            contentShow()
        }

    }

}