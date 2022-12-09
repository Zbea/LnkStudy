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
import kotlinx.android.synthetic.main.ac_bookstore_type.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class LauncherActivity : MainActivity() {

    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null

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

        iv_jc.setOnClickListener {
            gotoBookStore(0)
        }

        iv_gj.setOnClickListener {
            gotoBookStore(1)
        }

        iv_zrkx.setOnClickListener {
            gotoBookStore(2)
        }

        iv_shkx.setOnClickListener {
            gotoBookStore(3)
        }

        iv_swkx.setOnClickListener {
            gotoBookStore(4)
        }

        iv_ydcy.setOnClickListener {
            gotoBookStore(5)
        }

    }

    private fun gotoBookStore(type: Int) {
        val intent = Intent(this, BookStoreActivity::class.java)
        intent.flags = type
        customStartActivity(intent)
    }

    /**
     * 开始每天定时任务 下午三点
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
            selectLong = mCalendar.timeInMillis
        }

        val intent = Intent(this, AlarmService::class.java)
        intent.action = Constants.ACTION_UPLOAD
        pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP, selectLong,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

    }

    /**
     * 停止每天定时任务
     */
    private fun stopRemind() {
        alarmManager?.cancel(pendingIntent)
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