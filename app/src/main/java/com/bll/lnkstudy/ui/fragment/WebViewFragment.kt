package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.utils.IflytekVoiceRecognition
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.WebViewAssistClass
import com.bll.lnkstudy.utils.WebViewAssistClass.OnFragmentToActivityListener
import kotlinx.android.synthetic.main.fragment_webview.wv_view


class WebViewFragment:BaseFragment() {

    private var url=""
    private var isWebViewComplete=false

    var activityListener: OnFragmentToActivityListener? = null
    private var recognition: IflytekVoiceRecognition? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        // 超过2秒未收到信息，执行后续操作
        onTimeout()
    }
    private val TIMEOUT_MILLIS = 1000L // 超时时间：2秒
    private var voiceStr=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_webview
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        if (activity is OnFragmentToActivityListener) {
            activityListener = activity as OnFragmentToActivityListener
        }

        val webSettings = wv_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW// 允许混合内容
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true // 启用DOM存储（很多网页依赖此功能）
        webSettings.databaseEnabled = true // 启用数据库存储
        webSettings.loadWithOverviewMode = true // 自适应屏幕
        webSettings.useWideViewPort = true // 支持viewport
        webSettings.defaultTextEncodingName = "UTF-8" // 设置编码格式

        WebView.setWebContentsDebuggingEnabled(true)
        wv_view.addJavascriptInterface(WebViewAssistClass.JsInterface(this), "AndroidInterface")
        wv_view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isWebViewComplete=true
            }
        }
        wv_view.webChromeClient =object:WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest) {
                requireActivity().runOnUiThread {
                    val allowedPermissions = request.resources.filter {
                        it == PermissionRequest.RESOURCE_AUDIO_CAPTURE // 麦克风权限标识
                    }.toTypedArray()
                    request.grant(allowedPermissions)
                }
            }
        }

        initVoiceRecognition()
    }

    override fun lazyLoad() {
        val token= SPUtil.getString("token")
        val accountId=MethodManager.getAccountId()
        if (token.isNotEmpty()&&accountId!=0L){
            url = "https://inkbook.szvt.com?token=${token}&accountId=${accountId}"
            wv_view.loadUrl(url)
        }
    }

    fun clearWebViewCache() {
        // 清除网页缓存（包括磁盘缓存和内存缓存）
        wv_view.clearCache(true) // 参数 true 表示同时清除磁盘缓存，false 仅清除内存缓存
        // 清除历史记录
        wv_view.clearHistory()
        // 清除表单数据（如输入的账号密码等）
        wv_view.clearFormData()
        wv_view.loadUrl(url)
    }

    fun addJavascriptBackPath(id:String,path:String){
        val drawPath=path.split(',')[0].replace("'", "\\'")
        val mergePath=path.split(',')[1].replace("'", "\\'")
        val jsCode = ("javascript:window.receiveFromTargetActivity('$id,$drawPath,$mergePath')")
        wv_view.evaluateJavascript(jsCode) {}
    }

    private fun addJavascriptRefreshSubject(){
        val jsCode = ("javascript:window.refreshSubject()")
        wv_view.evaluateJavascript(jsCode) {}
    }

    private fun addJavascriptCloseWebView(){
        val jsCode = ("javascript:window.onStopVoicePlayback()")
        wv_view.evaluateJavascript(jsCode) {}
    }

    private fun addJavascriptRecognitionResult(type:Int,string: String){
        val content=string.replace("'", "\\'")
        val jsCode = ("javascript:window.onVoiceRecognitionResult('$type','$content')")
        wv_view.evaluateJavascript(jsCode) {}
    }

    /**
     * 初始化语音识别客户端
     */
    private fun initVoiceRecognition() {
        recognition = IflytekVoiceRecognition(
            "8fbb0406",
            "78234e008c2cbd219f62948383765f29",
            "Y2IwZmI0NGZmMjExNDc5M2I2MDQ0Y2Rh",
            object : IflytekVoiceRecognition.RecognitionListener {
                override fun onPartialResult(text: String) {
                    showLog(text)
                    voiceStr=text
                    startOrResetTimer()
                }
                override fun onFinalResult(text: String) {
                    showLog(text)
                    requireActivity().runOnUiThread {
                        addJavascriptRecognitionResult(1,text)
                    }
                }
                override fun onError(message: String) {
                    showLog("错误提示：$message")
                    requireActivity().runOnUiThread {
                        showToast(message)
                        destroyNativeVoiceRecognition()
                        addJavascriptRecognitionResult(0,message)
                    }
                }
                override fun onClose() {
                }
            }
        )
    }

    private fun startOrResetTimer() {
        // 移除已有的延迟任务（避免重复执行）
        handler.removeCallbacks(timeoutRunnable)
        // 重新发送延迟任务
        handler.postDelayed(timeoutRunnable, TIMEOUT_MILLIS)
    }

    private fun onTimeout(){
        showLog("识别结果：$voiceStr")
        addJavascriptRecognitionResult(1,voiceStr)
        destroyNativeVoiceRecognition()
    }

    // 启动原生语音识别
    fun startNativeVoiceRecognition() {
        try {
            recognition?.startCapture()
        } catch (e: Exception) {
            showToast("录音失败: " + e.message)
            addJavascriptRecognitionResult(0,"录音失败: " + e.message)
        }
    }

    fun stopNativeVoiceRecognition(){
        try {
            recognition?.stopAndSend()
        } catch (e: java.lang.Exception) {
            showToast("识别失败: " + e.message)
            addJavascriptRecognitionResult(0,"识别失败: " + e.message)
        }
    }

   fun destroyNativeVoiceRecognition(){
       voiceStr=""
       // 停止识别
       recognition?.stop()
       // 页面销毁时移除所有任务，避免内存泄漏
       handler.removeCallbacksAndMessages(null)
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
        destroyNativeVoiceRecognition()
    }

    override fun onRefreshData() {
        if (!isWebViewComplete)
            lazyLoad()
    }

    override fun onRefreshHideData() {
        addJavascriptCloseWebView()
    }

    override fun onPause() {
        super.onPause()
        addJavascriptCloseWebView()
    }

    override fun onNetworkConnectionSuccess() {
        if (!isWebViewComplete){
            lazyLoad()
        }
        else{
             wv_view.reload()
        }
    }
}