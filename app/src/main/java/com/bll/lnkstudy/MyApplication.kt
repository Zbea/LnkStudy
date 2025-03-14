package com.bll.lnkstudy

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bll.lnkstudy.greendao.DaoMaster
import com.bll.lnkstudy.greendao.DaoSession
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import com.liulishuo.filedownloader.FileDownloader
import kotlin.properties.Delegates


class MyApplication : Application(){


    companion object {

        private const val TAG = "MyApplication"

        var mContext: Context by Delegates.notNull()
            private set

        var mDaoSession:DaoSession?=null
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        requestQueue = Volley.newRequestQueue(applicationContext)
        SPUtil.init(this)
        NetworkUtil.init(this)
        SToast.initToast(this)
        FileDownloader.setup(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        setDatabase()

    }

    /**
     * 配置greenDao
     */
    private fun setDatabase() {
        val mHelper = GreenDaoUpgradeHelper(this, "lnkstudy.db" , null)
        val  db = mHelper.writableDatabase
        val mDaoMaster = DaoMaster(db)
        mDaoSession = mDaoMaster.newSession()
    }

    private val mActivityLifecycleCallbacks = object :ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "onCreated: " + activity.componentName.className)
            ActivityManager.getInstance().addActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {
            Log.d(TAG, "onResumed: " + activity.componentName.className)
        }

        override fun onActivityPaused(activity: Activity) {
            Log.d(TAG, "onPaused: " + activity.componentName.className)
        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            Log.d(TAG, "onActivitySaveInstanceState: " + activity.componentName.className)
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
//            Log.d(TAG, Log.getStackTraceString(Throwable()))
            ActivityManager.getInstance().finishActivity(activity)
        }
    }


}
