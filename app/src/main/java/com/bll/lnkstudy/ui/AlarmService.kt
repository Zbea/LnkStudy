package com.bll.lnkstudy.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_EVENT
import org.greenrobot.eventbus.EventBus

class AlarmService:Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("debug","全局自动打包上传")
        //开启全局自动打包上传
        EventBus.getDefault().postSticky(AUTO_UPLOAD_EVENT)

        return super.onStartCommand(intent, flags, startId)

    }

}