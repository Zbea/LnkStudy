package com.bll.lnkstudy.utils

import android.webkit.JavascriptInterface
import com.bll.lnkstudy.ui.fragment.WebViewFragment


class WebViewAssistClass {

    interface OnFragmentToActivityListener {
        fun startTargetActivityForResult(id: String,url: String)
    }

    class JsInterface(private val fragment: WebViewFragment) {
        @JavascriptInterface
        fun jumpToNativeActivity(id: String,url:String) {
            if (fragment.activityListener!=null)
                fragment.activityListener!!.startTargetActivityForResult(id,url)
        }

        @JavascriptInterface
        fun openVoiceRecognition() {
            fragment.startNativeVoiceRecognition()
        }

        @JavascriptInterface
        fun stopVoiceRecognition() {
            fragment.stopNativeVoiceRecognition()
        }

        @JavascriptInterface
        fun destroyVoiceRecognition() {
            fragment.destroyNativeVoiceRecognition()
        }
    }

}
