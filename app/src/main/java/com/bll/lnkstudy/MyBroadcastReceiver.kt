package com.bll.lnkstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bll.lnkstudy.ui.activity.SearchActivity
import com.bll.lnkstudy.utils.ActivityManager
import org.greenrobot.eventbus.EventBus

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            Constants.ACTION_UPLOAD->{
                Log.d("debug","全局自动打包上传")
                //开启全局自动打包上传
                EventBus.getDefault().post(Constants.AUTO_UPLOAD_EVENT)
            }
            Constants.ACTION_UPLOAD_2MONTH->{
                Log.d("debug","2月1日下学期开学")
                EventBus.getDefault().post(Constants.AUTO_UPLOAD_1MONTH_EVENT)
                //清除作业通知（每学期上学开始）
                EventBus.getDefault().post(Constants.MAIN_HOMEWORK_NOTICE_EVENT)
            }
            Constants.ACTION_UPLOAD_9MONTH->{
                Log.d("debug","9月1日升年级、清空")
                EventBus.getDefault().post(Constants.AUTO_UPLOAD_9MONTH_EVENT)
                //清除作业通知（每学期上学开始）
                EventBus.getDefault().post(Constants.MAIN_HOMEWORK_NOTICE_EVENT)
            }
            Constants.ACTION_EXAM_TIME->{
                Log.d("debug","考试提交")
                //学生自动提交
                EventBus.getDefault().post(Constants.EXAM_TIME_EVENT)
            }
            "com.android.settings.importdata"->{
                Log.d("debug","一键下载")
                EventBus.getDefault().post(Constants.DATA_DOWNLOAD_EVENT)
            }
            "com.android.settings.importrentdata"->{
                Log.d("debug","租用下载")
                EventBus.getDefault().post(Constants.DATA_RENT_EVENT)
            }
            "com.android.settings.cleardata"->{
                Log.d("debug","一键清除")
                EventBus.getDefault().post(Constants.DATA_CLEAT_EVENT)
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
                    DataUpdateManager.createDataUpdate(6,bookId.toInt(),2,bookId.toInt(),"",path!!)
                }
            }
            "android.intent.action.PACKAGE_ADDED"->{
                Log.d("debug","刷新应用列表")
                EventBus.getDefault().post(Constants.APP_INSTALL_EVENT)
            }
            "android.intent.action.PACKAGE_REMOVED"->{
                Log.d("debug","刷新应用列表")
                EventBus.getDefault().post(Constants.APP_UNINSTALL_EVENT)
            }
        }
    }
}