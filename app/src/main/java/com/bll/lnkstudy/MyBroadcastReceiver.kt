package com.bll.lnkstudy

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.bll.lnkstudy.manager.DataUpdateDaoManager
import com.bll.lnkstudy.ui.activity.SearchActivity
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import org.greenrobot.eventbus.EventBus


open class MyBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        //未登录不执行
        if (!MethodManager.isLogin()){
            return
        }
        when(intent.action){
            Constants.ACTION_DAY_REFRESH->{
                Log.d("debug","每天刷新")
                EventBus.getDefault().postSticky(Constants.AUTO_REFRESH_EVENT)
            }
            Constants.ACTION_UPLOAD_15->{
                Log.d("debug","15点增量上传、以及全局刷新")
                MethodManager.wakeUpScreen(context)
                if (!NetworkUtil(context).isNetworkConnected()){
                    NetworkUtil(context).toggleNetwork(true)
                }
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_EVENT)
            }
            Constants.ACTION_UPLOAD_NEXT_SEMESTER->{
                Log.d("debug","2月1日下学期开学")
                MethodManager.wakeUpScreen(context)
                if (!NetworkUtil(context).isNetworkConnected()){
                    NetworkUtil(context).toggleNetwork(true)
                }
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT)
            }
            Constants.ACTION_UPLOAD_YEAR->{
                Log.d("debug","每年上传")
                MethodManager.wakeUpScreen(context)
                if (!NetworkUtil(context).isNetworkConnected()){
                    NetworkUtil(context).toggleNetwork(true)
                }
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_YEAR_EVENT)
            }
            Constants.ACTION_UPLOAD_LAST_SEMESTER->{
                Log.d("debug","9月1日升年级、清空")
                MethodManager.wakeUpScreen(context)
                if (!NetworkUtil(context).isNetworkConnected()){
                    NetworkUtil(context).toggleNetwork(true)
                }
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT)
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
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_RIGHT)
                    context.startActivity(intent)
                }
            }
            //阅读器回传
            "com.geniatech.knote.reader.save.note.broadcast"->{
                val bookId=intent.getStringExtra("key_book_id")
                val path=intent.getStringExtra("note_path")
                val typeId=intent.getIntExtra("type",0)
                val type=if (typeId==1)6 else 1

                val item=DataUpdateDaoManager.getInstance().queryBean(type,bookId!!.toInt(),2)
                if (item==null){
                    DataUpdateManager.createDataUpdateDrawing(type,bookId.toInt(),2,path!!)
                }
                else{
                    DataUpdateManager.editDataUpdate(type,bookId.toInt(),2)
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
                val info: NetworkInfo? = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (info!!.state.equals(NetworkInfo.State.CONNECTED)) {
                    val isNet = NetworkInfo.State.CONNECTED == info.state && info.isAvailable
                    Log.d("debug", "监听网络变化$isNet")
                    if (isNet)
                        EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
                if (info.state.equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.d("debug", "监听网络变化false")
                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_FAIL_EVENT)
                }
            }
            //wifi监听
//            WifiManager.NETWORK_STATE_CHANGED_ACTION->{
//                val info: NetworkInfo? = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
//                if (info!!.state.equals(NetworkInfo.State.CONNECTED)) {
//                    val isNet = NetworkInfo.State.CONNECTED == info.state && info.isAvailable
//                    Log.d("debug", "wifi监听网络变化$isNet")
//                    if (isNet)
//                        EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
//                }
//                if (info.state.equals(NetworkInfo.State.DISCONNECTED)) {
//                    Log.d("debug", "wifi监听网络变化false")
//                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_FAIL_EVENT)
//                }
//            }
            Constants.APP_NET_REFRESH->{
                if (NetworkUtil(context).isNetworkConnected()){
                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
            }
        }
    }
}