package com.bll.lnkstudy.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.ui.AlarmService
import kotlinx.android.synthetic.main.ac_bookstore_type.*
import java.util.*

class LauncherActivity : MainActivity() {

    override fun layoutId(): Int {
        return R.layout.ac_launcher
    }

    override fun initView() {
        super.initView()
        startRemind()

        iv_jc.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "教材")
            )
        }

        iv_gj.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "古籍")
            )
        }

        iv_zrkx.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "自然科学")
            )
        }

        iv_shkx.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "社会科学")
            )
        }

        iv_swkx.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "思维科学")
            )
        }

        iv_ydcy.setOnClickListener {
            startActivity(
                Intent(this, BookStoreActivity::class.java)
                    .putExtra("title", "运动才艺")
            )
        }
    }

    /**
     * 开始每天定时任务
     */
    private fun startRemind() {

        val mCalendar = Calendar.getInstance()
        val currentTimeMillisLong = System.currentTimeMillis()
        mCalendar.timeInMillis = currentTimeMillisLong
        mCalendar.timeZone = TimeZone.getTimeZone("GMT+8")
        mCalendar.set(Calendar.HOUR_OF_DAY, 15)
        mCalendar.set(Calendar.MINUTE, 0)
        mCalendar.set(Calendar.SECOND, 0)
        mCalendar.set(Calendar.MILLISECOND, 0)

        var selectLong = mCalendar.timeInMillis

        if (currentTimeMillisLong > selectLong) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(this, AlarmService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, selectLong,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

    }

    /**
     * 停止每天定时任务
     */
    private fun stopRemind() {
        val intent = Intent(this, AlarmService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode === KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRemind()
    }



}