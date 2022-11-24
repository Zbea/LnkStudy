package com.bll.lnkstudy.ui.activity.date

import android.view.View
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.PopWindowDateDayRemind
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_date_day_details.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class DateDayDetailsActivity : BaseAppCompatActivity() {

    private var flags=0
    private val nowDate = DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var dateEvent: DateEvent? = null
    private var oldEvent:DateEvent?=null
    private var popRemind: PopWindowDateDayRemind? = null
    private var dateDialog:DateDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_date_day_details
    }

    override fun initData() {
        flags=intent.flags
        if (flags == 0) {
            dateEvent = DateEvent()
            dateEvent?.type=1
        } else {
            dateEvent = intent.getBundleExtra("bundle").getSerializable("dateEvent") as DateEvent
            oldEvent=dateEvent?.clone() as DateEvent
            et_title.setText(dateEvent?.title)
            tv_date.text = dateEvent?.dayLongStr
            tv_countdown.text = "还有" + DateUtils.sublongToDay(dateEvent?.dayLong!!, nowDate) + "天"
            sh_countdown.isChecked = dateEvent?.isCountdown == true
            tv_countdown.visibility=if (dateEvent?.isCountdown == true) View.VISIBLE else View.GONE
            tv_remind.text="${dateEvent?.remindDay}天"
            sh_remind.isChecked = dateEvent?.isRemind == true
            ll_remind.visibility=if (dateEvent?.isRemind == true) View.VISIBLE else View.GONE
            rl_bell.visibility=if (dateEvent?.isRemind == true) View.VISIBLE else View.INVISIBLE
        }

    }

    override fun initView() {
        setPageTitle("重要日子")
        setPageSetting("保存")

        tv_date.setOnClickListener {

            if (dateDialog==null){
                dateDialog=DateDialog(this).builder()
                dateDialog?.setOnDateListener { dateStr, dateTim ->
                    if (dateTim >= nowDate) {
                        dateEvent?.dayLong = dateTim
                        dateEvent?.dayLongStr = dateStr
                        tv_date.text = dateStr
                        tv_countdown.text = "还有" + DateUtils.sublongToDay(dateTim, nowDate) + "天"
                    }
                }
            }
            else{
                dateDialog?.show()
            }
        }

        sh_countdown.setOnCheckedChangeListener { p0, isCheck ->
            if (isCheck) {
                showView(tv_countdown)
            } else {
                disMissView(tv_countdown)
            }
            dateEvent?.isCountdown = isCheck
        }

        sh_remind.setOnCheckedChangeListener { p0, isCheck ->
            if (isCheck) {
                showView(rl_bell, ll_remind)
            } else {
                disMissView( ll_remind)
                rl_bell.visibility=View.INVISIBLE
            }
            dateEvent?.isRemind = isCheck
        }

        tv_remind.setOnClickListener {
            showRemind()
        }

        tv_setting.setOnClickListener {
            val titleStr = et_title.text.toString()
            if (titleStr.isNullOrEmpty()) {
                showToast("请输入标题")
                return@setOnClickListener
            }
            dateEvent?.title = titleStr
            if (dateEvent?.dayLongStr.isNullOrEmpty()) {
                showToast("请选择日期")
                return@setOnClickListener
            }
            //删除原来的日历
            if (oldEvent!=null){
                showLog(oldEvent?.title!!)
                CalendarReminderUtils.deleteCalendarEvent(this,oldEvent?.title)
            }

            DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEvent)
            if(dateEvent?.isRemind==true){
                CalendarReminderUtils.addCalendarEvent2(this,titleStr,dateEvent?.dayLong!!,dateEvent?.remindDay!!)
            }
            EventBus.getDefault().post(Constants.DATE_EVENT)
            finish()
        }

    }

    /**
     * 选择提前天数
     */
    private fun showRemind() {
        if (popRemind == null) {
            popRemind = PopWindowDateDayRemind(this, tv_remind, dateEvent?.remindDay!!).builder()
            popRemind?.setOnSelectListener {
                tv_remind.text = it.remind
                dateEvent?.remindDay = it.remindIn
            }
        } else {
            popRemind?.show()
        }
    }

}