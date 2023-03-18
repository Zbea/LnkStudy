package com.bll.lnkstudy.ui.activity.date

import android.view.View
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.PopupDateDayRemind
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEventBean
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
    private var dateEventBean: DateEventBean? = null
    private var oldEvent: DateEventBean?=null
    private var popRemind: PopupDateDayRemind? = null
    private var dateDialog:DateDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_date_day_details
    }

    override fun initData() {
        flags=intent.flags
        if (flags == 0) {
            dateEventBean = DateEventBean()
            dateEventBean?.type=1
        } else {
            dateEventBean = intent.getBundleExtra("bundle")?.getSerializable("dateEvent") as DateEventBean
            oldEvent=dateEventBean?.clone() as DateEventBean
            et_title.setText(dateEventBean?.title)
            tv_date.text = dateEventBean?.dayLongStr
            tv_countdown.text = "还有" + DateUtils.sublongToDay(dateEventBean?.dayLong!!, nowDate) + "天"
            sh_countdown.isChecked = dateEventBean?.isCountdown == true
            tv_countdown.visibility=if (dateEventBean?.isCountdown == true) View.VISIBLE else View.GONE
            tv_remind.text="${dateEventBean?.remindDay}天"
            sh_remind.isChecked = dateEventBean?.isRemind == true
            ll_remind.visibility=if (dateEventBean?.isRemind == true) View.VISIBLE else View.GONE
            rl_bell.visibility=if (dateEventBean?.isRemind == true) View.VISIBLE else View.INVISIBLE
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
                        dateEventBean?.dayLong = dateTim
                        dateEventBean?.dayLongStr = dateStr
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
            dateEventBean?.isCountdown = isCheck
        }

        sh_remind.setOnCheckedChangeListener { p0, isCheck ->
            if (isCheck) {
                showView(rl_bell, ll_remind)
            } else {
                disMissView( ll_remind)
                rl_bell.visibility=View.INVISIBLE
            }
            dateEventBean?.isRemind = isCheck
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
            dateEventBean?.title = titleStr
            if (dateEventBean?.dayLongStr.isNullOrEmpty()) {
                showToast("请选择日期")
                return@setOnClickListener
            }
            //删除原来的日历
            if (oldEvent!=null){
                showLog(oldEvent?.title!!)
                CalendarReminderUtils.deleteCalendarEvent(this,oldEvent?.title)
            }

            DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEventBean)
            if(dateEventBean?.isRemind==true){
                CalendarReminderUtils.addCalendarEvent2(this,titleStr,dateEventBean?.dayLong!!,dateEventBean?.remindDay!!)
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
            popRemind = PopupDateDayRemind(this, tv_remind, dateEventBean?.remindDay!!).builder()
            popRemind?.setOnSelectListener {
                tv_remind.text = it.remind
                dateEventBean?.remindDay = it.remindIn
            }
        } else {
            popRemind?.show()
        }
    }

}