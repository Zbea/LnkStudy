package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.utils.WebViewAssistClass
import com.bll.lnkstudy.utils.WebViewAssistClass.OnFragmentToActivityListener
import kotlinx.android.synthetic.main.fragment_webview.wv_view


class WebViewFragment:BaseFragment() {

    private var url=""
    private var isWebViewComplete=false

    var activityListener: OnFragmentToActivityListener? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_webview
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        if (activity is OnFragmentToActivityListener) {
            activityListener = activity as OnFragmentToActivityListener
        }

        url = "https://test-inkbook.szvt.com?token=${MethodManager.getUser().token}&accountId=${MethodManager.getUser().accountId}"
        wv_view.loadUrl(url)

        val webSettings = wv_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = false
        webSettings.domStorageEnabled = true // 启用DOM存储（很多网页依赖此功能）
        webSettings.databaseEnabled = true // 启用数据库存储
        webSettings.loadWithOverviewMode = true // 自适应屏幕
        webSettings.useWideViewPort = true // 支持viewport
        webSettings.defaultTextEncodingName = "UTF-8" // 设置编码格式
        webSettings.allowFileAccessFromFileURLs = false // 禁止文件URL访问其他文件（安全）
        webSettings.allowUniversalAccessFromFileURLs = false // 禁止文件URL访问所有资源（安全）
        webSettings.textZoom = 100

        WebView.setWebContentsDebuggingEnabled(true)
        wv_view.addJavascriptInterface(WebViewAssistClass.JsInterface(this), "AndroidInterface")
        wv_view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isWebViewComplete=true
            }
        }
        wv_view.webChromeClient = WebChromeClient()

    }

    override fun lazyLoad() {
    }

    fun addJavascriptBackPath(path:String){
        showLog(path)
        val jsCode = ("javascript:window.receiveFromTargetActivity('" + path.replace("'", "\\'")) + "')"
        wv_view.evaluateJavascript(jsCode) {}
    }

    private fun addJavascriptRefreshSubject(){
        val jsCode = ("javascript:window.refreshSubject()")
        wv_view.evaluateJavascript(jsCode) {}
    }

    fun onBackPressed(){
        if (wv_view != null && wv_view.canGoBack()) {
            wv_view.goBack()
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.COURSEITEM_EVENT){
            addJavascriptRefreshSubject()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (wv_view != null) {
            val parent = wv_view.parent as ViewGroup
            parent.removeView(wv_view)
            wv_view.removeAllViews()
            wv_view.destroy()
        }
    }

    override fun onRefreshData() {
        if (!isWebViewComplete)
            wv_view.loadUrl(url)
    }

    override fun onNetworkConnectionSuccess() {
        wv_view.loadUrl(url)
    }
}