package com.bll.lnkstudy.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MyBroadcastReceiver
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CampusModeVideoDialog
import com.bll.lnkstudy.mvp.model.EventBusData
import com.bll.lnkstudy.mvp.model.ItemList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class CampusModeActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<ItemList>()
    private var alarmManagers= mutableListOf<AlarmManager>()
    private var pendingIntents= mutableListOf<PendingIntent>()

    override fun layoutId(): Int {
        return R.layout.ac_campus_mode
    }

    override fun initData() {
        val list= ItemList()
        list.id=1
        list.name="眼保健操"
        list.date="11:05"
        lists.add(list)

        val list1= ItemList()
        list1.id=2
        list1.name="课间操"
        list1.date="14:05"
        lists.add(list1)

        for (item in lists){
            startAlarmManager(item)
        }

    }

    override fun initView() {
        EventBus.getDefault().register(this)
    }

    /**
     * 开始定时任务
     */
    private fun startAlarmManager(itemList: ItemList){
        val simpleDateFormat=SimpleDateFormat("HH:mm")
        val date=simpleDateFormat.parse(itemList.date)

        val mCalendar = Calendar.getInstance()
        val currentTimeMillisLong = System.currentTimeMillis()
        mCalendar.timeInMillis = currentTimeMillisLong
        mCalendar.timeZone = TimeZone.getTimeZone("GMT+8")
        mCalendar.set(Calendar.HOUR_OF_DAY, date.hours)
        mCalendar.set(Calendar.MINUTE, date.minutes)
        mCalendar.set(Calendar.SECOND, 0)
        mCalendar.set(Calendar.MILLISECOND, 0)

        var selectLong = mCalendar.timeInMillis

        if (currentTimeMillisLong > selectLong) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1)
            selectLong = mCalendar.timeInMillis
        }

        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.putExtra("id",itemList.id)
        val pendingIntent = PendingIntent.getService(this, itemList.id, intent, 0)
        pendingIntents.add(pendingIntent)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, selectLong,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        alarmManagers.add(alarmManager)
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(bean: EventBusData) {
        if (bean.event== Constants.VIDEO_EVENT){
            val id=bean.id-1
            CampusModeVideoDialog(this,lists[id]).builder()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //取消定时任务
        for (i in alarmManagers.indices){
            alarmManagers[i].cancel(pendingIntents[i])
        }
        EventBus.getDefault().unregister(this)
    }

}