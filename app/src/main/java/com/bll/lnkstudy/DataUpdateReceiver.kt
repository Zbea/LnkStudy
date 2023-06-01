package com.bll.lnkstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bll.lnkstudy.ui.activity.SearchActivity
import com.bll.lnkstudy.utils.ActivityManager
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
            "ACTION_GLOBAL_SEARCH"->{
                Log.d("debug","收到搜索事件")
                ActivityManager.getInstance().finishActivity(SearchActivity::class.java.name)
                val intent = Intent(context,SearchActivity::class.java)
                intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 2)
                context.startActivity(intent)
            }
        }
    }
}