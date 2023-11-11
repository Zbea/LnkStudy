package com.bll.lnkstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log
import com.bll.lnkstudy.ui.activity.SearchActivity
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import org.greenrobot.eventbus.EventBus

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            Constants.ACTION_UPLOAD_8->{
                Log.d("debug","8点全局刷新")
                EventBus.getDefault().postSticky(Constants.APP_REFRESH_EVENT)
            }
            Constants.ACTION_UPLOAD_15->{
                Log.d("debug","15点增量上传、以及全局刷新")
                //开启全局自动打包上传
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_EVENT)
                EventBus.getDefault().postSticky(Constants.APP_REFRESH_EVENT)
            }
            Constants.ACTION_UPLOAD_18->{
                Log.d("debug","18点全局刷新")
                EventBus.getDefault().postSticky(Constants.APP_REFRESH_EVENT)
            }
            Constants.ACTION_UPLOAD_NEXT_SEMESTER->{
                Log.d("debug","2月1日下学期开学")
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT)
                //清除作业通知（每学期上学开始）
                EventBus.getDefault().postSticky(Constants.MAIN_HOMEWORK_NOTICE_EVENT)
            }
            Constants.ACTION_UPLOAD_LAST_SEMESTER->{
                Log.d("debug","9月1日升年级、清空")
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT)
                //清除作业通知（每学期上学开始）
                EventBus.getDefault().postSticky(Constants.MAIN_HOMEWORK_NOTICE_EVENT)
            }
            Constants.ACTION_EXAM_TIME->{
                Log.d("debug","考试提交")
                //学生自动提交
                EventBus.getDefault().post(Constants.EXAM_TIME_EVENT)
            }
            "com.android.settings.importdata"->{
                Log.d("debug","一键下载")
                EventBus.getDefault().post(Constants.SETTING_DOWNLOAD_EVENT)
            }
            "com.android.settings.importrentdata"->{
                Log.d("debug","租用下载")
                EventBus.getDefault().post(Constants.SETTING_RENT_EVENT)
            }
            "com.android.settings.cleardata"->{
                Log.d("debug","一键清除")
                EventBus.getDefault().post(Constants.SETTING_CLEAT_EVENT)
            }
            "ACTION_GLOBAL_SEARCH"->{
                Log.d("debug","搜索")
                val token=SPUtil.getString("token")
                //判断是否登录
                if (token.isNotEmpty()){
                    ActivityManager.getInstance().finishActivity(SearchActivity::class.java.name)
                    val intent = Intent(context,SearchActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 2)
                    context.startActivity(intent)
                }
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
            //监听网络变化
            ConnectivityManager.CONNECTIVITY_ACTION->{
                val isNet=intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                Log.d("debug", "监听网络变化$isNet")
                EventBus.getDefault().post(if (isNet) Constants.NETWORK_CONNECTION_COMPLETE_EVENT else Constants.NETWORK_CONNECTION_FAIL_EVENT)
            }
            //wifi监听
            WifiManager.NETWORK_STATE_CHANGED_ACTION->{
                val info: NetworkInfo? = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
                if (info!!.state.equals(NetworkInfo.State.CONNECTED)) {
                    val isNet=NetworkUtil(context).isNetworkConnected()
                    Log.d("debug", "wifi监听网络变化$isNet")
                    if (isNet)
                        EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
            }
        }
    }
}