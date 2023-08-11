package com.bll.lnkstudy

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_1MONTH_EVENT
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_9MONTH_EVENT
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_EVENT
import com.bll.lnkstudy.Constants.Companion.EXAM_TIME_EVENT
import com.bll.lnkstudy.mvp.model.EventBusData
import org.greenrobot.eventbus.EventBus

/**
 * 定时服务
 */
class AlarmService:Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action=intent?.action
        val state=intent?.getIntExtra("id",0)
        when(action){
            Constants.ACTION_UPLOAD->{
                Log.d("debug","全局自动打包上传")
                //开启全局自动打包上传
                EventBus.getDefault().postSticky(AUTO_UPLOAD_EVENT)
            }
            Constants.ACTION_UPLOAD_1MONTH->{
                Log.d("debug","1月1日日记上传")
                //开启全局自动打包上传
                EventBus.getDefault().postSticky(AUTO_UPLOAD_1MONTH_EVENT)
            }
            Constants.ACTION_UPLOAD_9MONTH->{
                Log.d("debug","9月1课本、作业、考卷、书画")
                //开启全局自动打包上传
                EventBus.getDefault().postSticky(AUTO_UPLOAD_9MONTH_EVENT)
            }
            Constants.ACTION_VIDEO->{
                Log.d("debug",state.toString())
                val eventBusBean= EventBusData()
                eventBusBean.event=Constants.VIDEO_EVENT
                eventBusBean.id=state!!
                EventBus.getDefault().post(eventBusBean)
            }
            Constants.ACTION_EXAM_TIME->{
                Log.d("debug","考试提交")
                val eventBusBean= EventBusData()
                eventBusBean.event=EXAM_TIME_EVENT
                //学生自动提交
                EventBus.getDefault().post(eventBusBean)
            }
        }

        return super.onStartCommand(intent, flags, startId)

    }

}