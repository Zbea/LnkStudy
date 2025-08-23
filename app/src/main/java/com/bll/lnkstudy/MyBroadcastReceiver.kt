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
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import org.greenrobot.eventbus.EventBus
import java.io.File


open class MyBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            Constants.ACTION_DAY_REFRESH->{
                Log.d(Constants.DEBUG,"每天刷新")
                EventBus.getDefault().postSticky(Constants.AUTO_REFRESH_EVENT)
            }
            Constants.ACTION_UPLOAD_15->{
                Log.d(Constants.DEBUG,"13点增量上传、以及全局刷新")
                MethodManager.wakeUpScreen(context)
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_EVENT)
            }
            Constants.ACTION_UPLOAD_YEAR->{
                Log.d(Constants.DEBUG,"每年上传")
                MethodManager.wakeUpScreen(context)
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_YEAR_EVENT)
            }
            Constants.ACTION_UPLOAD_NEXT_SEMESTER->{
                Log.d(Constants.DEBUG,"2月1日下学期开学")
                MethodManager.wakeUpScreen(context)
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT)
            }
            Constants.ACTION_UPLOAD_LAST_SEMESTER->{
                Log.d(Constants.DEBUG,"8月1日升年级、清空")
                MethodManager.wakeUpScreen(context)
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT)
            }
            Constants.ACTION_EXAM_TIME->{
                Log.d(Constants.DEBUG,"考试提交")
                //学生自动提交
                EventBus.getDefault().post(Constants.EXAM_TIME_EVENT)
            }
            "com.android.settings.importdata"->{
                Log.d(Constants.DEBUG,"一键下载")
                EventBus.getDefault().post(Constants.SETTING_DOWNLOAD_EVENT)
            }
            "com.android.settings.importrentdata"->{
                Log.d(Constants.DEBUG,"租用下载")
                EventBus.getDefault().post(Constants.SETTING_RENT_EVENT)
            }
            "com.android.settings.cleardata"->{
                Log.d(Constants.DEBUG,"一键清除")
            }
            "ACTION_GLOBAL_SEARCH"->{
                Log.d(Constants.DEBUG,"搜索")
                ActivityManager.getInstance().finishActivity(SearchActivity::class.java.name)
                val intent = Intent(context,SearchActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_RIGHT)
                context.startActivity(intent)
            }
            //阅读器回传
            "com.geniatech.knote.reader.save.note.broadcast"->{
                val bookId=intent.getStringExtra("key_book_id")
                val path=intent.getStringExtra("note_path")

                val item=DataUpdateDaoManager.getInstance().queryBean(6,bookId!!.toInt(),2,bookId.toInt())
                if (item==null){
                    DataUpdateManager.createDataUpdateDrawing(6,bookId.toInt(),2,bookId.toInt(),path!!)
                }
                else{
                    DataUpdateManager.editDataUpdate(6,bookId.toInt(),2,bookId.toInt())
                }
            }
            "android.intent.action.PACKAGE_ADDED"->{
                Log.d(Constants.DEBUG,"刷新应用列表")
                EventBus.getDefault().post(Constants.APP_INSTALL_EVENT)
                if (intent.data?.schemeSpecificPart.equals(context.packageName)) {
                    Log.d(Constants.DEBUG,"更新launcher")
                    //安装完成后删除
                    FileUtils.deleteFile(File(FileAddress().getLauncherPath()))
                    // 应用安装完成后重启
                    AppUtils.reOpenApk(context)
                }
            }
            "android.intent.action.PACKAGE_REMOVED"->{
                Log.d(Constants.DEBUG,"刷新应用列表")
                EventBus.getDefault().post(Constants.APP_UNINSTALL_EVENT)
            }
            //监听网络变化
            ConnectivityManager.CONNECTIVITY_ACTION->{
                val info: NetworkInfo? = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (info!!.state.equals(NetworkInfo.State.CONNECTED)) {
                    val isNet = NetworkInfo.State.CONNECTED == info.state && info.isAvailable
                    Log.d(Constants.DEBUG, "监听网络变化$isNet")
                    if (isNet)
                        EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
            }
            Constants.APP_NET_REFRESH->{
                if (NetworkUtil.isNetworkConnected()){
                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
            }
        }
    }
}