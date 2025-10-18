package com.bll.lnkstudy.utils

import android.telecom.Log
import android.webkit.JavascriptInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.ui.fragment.WebViewFragment


class WebViewAssistClass {

    interface OnFragmentToActivityListener {
        fun startTargetActivityForResult(dataFromWeb: String)
    }

    class JsInterface(private val fragment: WebViewFragment) {
        @JavascriptInterface
        fun jumpToNativeActivity(message: String) {
            Log.d(Constants.DEBUG,"开始跳转")
            if (fragment.activityListener!=null)
                fragment.activityListener!!.startTargetActivityForResult(message)
        }
    }

}
