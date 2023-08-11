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
            "com.android.settings.importdata"->{
                Log.d("debug","一键下载")
                EventBus.getDefault().postSticky(Constants.DATA_DOWNLOAD_EVENT)
            }
            "com.android.settings.importrentdata"->{
                Log.d("debug","租用下载")
                EventBus.getDefault().postSticky(Constants.DATA_RENT_EVENT)
            }
            "com.android.settings.cleardata"->{
                Log.d("debug","一键清除")
                EventBus.getDefault().postSticky(Constants.DATA_CLEAT_EVENT)
            }
            "ACTION_GLOBAL_SEARCH"->{
                Log.d("debug","搜索")
                ActivityManager.getInstance().finishActivity(SearchActivity::class.java.name)
                val intent = Intent(context,SearchActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 2)
                context.startActivity(intent)
            }
            //阅读器回传
            "com.geniatech.knote.reader.save.note.broadcast"->{
                Log.d("debug",intent.getStringExtra("key_book_id")!!)
                Log.d("debug",intent.getStringExtra("note_path")!!)
                val bookId=intent.getStringExtra("key_book_id")
                val path=intent.getStringExtra("note_path")
                if (!bookId.isNullOrEmpty()){
                    //创建增量更新
                    DataUpdateManager.createDataUpdate(6,bookId.toInt(),2,bookId.toInt()
                        ,"",path!!)
                }
            }
        }
    }
}