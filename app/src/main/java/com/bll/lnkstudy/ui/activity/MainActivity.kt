package com.bll.lnkstudy.ui.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.ui.AlarmService
import com.bll.lnkstudy.utils.DateUtils
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class MainActivity : HomeLeftActivity(){

    override fun layoutId(): Int {
        return R.layout.ac_launcher
    }

    override fun initView() {
        super.initView()

        EasyPermissions.requestPermissions(
            this, "请求权限", 1,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECORD_AUDIO
        )

        startRemind()
        startRemind1Month()
        startRemind9Month()
    }

    /**
     * 开始每天定时任务 下午三点
     */
    private fun startRemind() {

         Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis

            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
             alarmManager.setRepeating(
                 AlarmManager.RTC_WAKEUP, selectLong,
                 AlarmManager.INTERVAL_DAY, pendingIntent
             )
        }


    }

    /**
     * 每年9月1 3点执行
     */
    private fun startRemind9Month() {
        val date=365*24*60*60*1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH,8)
            set(Calendar.DAY_OF_MONTH,1)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis()>selectLong){
                set(Calendar.YEAR,DateUtils.getYear()+1)
                selectLong=timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD_9MONTH
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 每年1月1 3点执行
     */
    private fun startRemind1Month() {
        val date=365*24*60*60*1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH,0)
            set(Calendar.DAY_OF_MONTH,1)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis()>selectLong){
                set(Calendar.YEAR,DateUtils.getYear()+1)
                selectLong=timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD_1MONTH
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }


}