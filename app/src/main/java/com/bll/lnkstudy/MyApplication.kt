package com.bll.lnkstudy

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import com.liulishuo.filedownloader.FileDownloader
import kotlin.properties.Delegates


class MyApplication : Application(){


    companion object {

        private val TAG = "MyApplication"

        var mContext: Context by Delegates.notNull()
            private set


    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        SPUtil.init(this)
        SToast.initToast(this)
        FileDownloader.setup(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)

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

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
            ActivityManager.getInstance().finishActivity(activity)

        }
    }


}
