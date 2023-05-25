package com.bll.lnkstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.greenrobot.eventbus.EventBus

class DataUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            "com.android.settings.downloaddata"->{
                Log.d("debug","开始一键下载")
                EventBus.getDefault().postSticky(Constants.DATA_DOWNLOAD_EVENT)
            }
            "com.android.settings.cleardata"->{
                Log.d("debug","开始一键清除")
                EventBus.getDefault().postSticky(Constants.DATA_CLEAT_EVENT)
            }
        }
    }
}