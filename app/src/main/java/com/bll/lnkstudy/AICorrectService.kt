package com.bll.lnkstudy

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.aiv.aisdk.NativeLib
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bll.lnkstudy.mvp.model.AIScoreItem
import com.bll.lnkstudy.mvp.model.AiRequestItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ScoreItemUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


class AICorrectService:Service() {
    private val TAG = "AICorrectService"

    private val AI_MESSAGE_TIMEOUT = 90 * 1000L // AI消息超时：30秒
    private val UPLOAD_MAX_RETRY = 2 // 上传最大重试次数：2次（共3次机会：1次原始+2次重试）
    private val UPLOAD_RETRY_INTERVAL = 5 * 1000L // 重试间隔：5秒
    private var nativeLib: NativeLib? = null
    private val gson = Gson()

    // 重试计数器（记录已重试次数，初始为0）
    private var uploadRetryCount = 0
    // 后台线程Handler（用于延迟重试，避免阻塞）
    private val backgroundHandler = Handler(Looper.getMainLooper()) // 若需纯后台线程，可自定义Looper
    // AI消息接收状态、超时计时器
    private var isAiMessageReceived = false
    private var aiMessageTimer: CountDownTimer? = null

    private var homeworkCommitInfoItem: HomeworkCommitInfoItem? = null
    // 缓存上传参数（避免重试时重复构造）
    private var cachedUploadMap: HashMap<String, Any>? = null


    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        initNativeLibAndMqtt()
        initAiMessageTimer() // 初始化AI消息超时计时器
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        homeworkCommitInfoItem = intent?.getSerializableExtra("KEY_HOMEWORK_COMMIT_ITEM") as? HomeworkCommitInfoItem

        //延迟执行
        backgroundHandler.postDelayed({
            sendAiCorrectRequest()
        }, 10*1000)

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 初始化AI消息超时计时器
     */
    private fun initAiMessageTimer() {
        aiMessageTimer = object : CountDownTimer(AI_MESSAGE_TIMEOUT, AI_MESSAGE_TIMEOUT) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (!isAiMessageReceived) {
                    stopServiceWithReason("AI消息超时")
                }
            }
        }.start()
    }

    /**
     * 初始化MQTT
     */
    private fun initNativeLibAndMqtt() {
        nativeLib = NativeLib()
        val clientId = "client_" + MethodManager.getUser().accountId
        nativeLib?.netConn(clientId, 123, object : NativeLib.MqttCallback {
            override fun onCallback(message: String?) {
                Log.d(TAG, "收到AI返回的message: $message")
                isAiMessageReceived = true
                aiMessageTimer?.cancel()
                handleAiMessage(message) // 处理AI消息（含重试逻辑）
            }
        })
    }

    /**
     * 发送AI请求
     */
    private fun sendAiCorrectRequest() {

        val aiRequestItem = AiRequestItem()
        aiRequestItem.subject = DataBeanManager.getCourseStr_en(homeworkCommitInfoItem?.course!!)

        if (!homeworkCommitInfoItem?.answerUrl.isNullOrEmpty()) {
            aiRequestItem.prompt = "参照上传的标准答案批改试题"
            val answerImages = homeworkCommitInfoItem?.answerUrl!!.split(",")
            for (imageUrl in answerImages) {
                val imageItem = AiRequestItem.ImageItem().apply {
                    image_url = AiRequestItem.ImageItem.ImageUrlItem().apply { url = imageUrl.trim() }
                }
                aiRequestItem.answer.add(imageItem)
            }
        } else {
            aiRequestItem.prompt = "批改试题"
        }
        val imageUrls = homeworkCommitInfoItem?.commitUrl!!.split(",")
        for (imageUrl in imageUrls) {
            val imageItem = AiRequestItem.ImageItem().apply {
                image_url = AiRequestItem.ImageItem.ImageUrlItem().apply { url = imageUrl.trim() }
            }
            aiRequestItem.question.add(imageItem)
        }

        val aiRequestJson = gson.toJson(aiRequestItem).trimIndent()
        nativeLib?.sendMessage(aiRequestJson, -1).apply{
            Log.d(TAG, "发送AI批改请求: $aiRequestJson")
        }
    }

    /**
     * 处理AI消息
     */
    private fun handleAiMessage(message: String?) {
        if (message.isNullOrEmpty()) {
            stopServiceWithReason("AI消息为空")
            return
        }
        try {
        // 1. 解析AI分数
        val alScoreItem = gson.fromJson(message, AIScoreItem::class.java)
        if (alScoreItem?.result?.choices.isNullOrEmpty()) {
            stopServiceWithReason("AI批改错误")
            return
        }

        val messageStr = alScoreItem.result.choices[0].message.content
        val scoreJson = ScoreItemUtils.getAIJsonScore(messageStr)
        Log.d(TAG, "AI解析json: $scoreJson")
        val scoreListType = object : TypeToken<MutableList<ScoreItem>>() {}.type
        val scoreList = gson.fromJson<MutableList<ScoreItem>>(scoreJson, scoreListType)
        val totalScore = scoreList.sumOf { it.score }

        // 2. 合并分数（原有逻辑）
        val currentScores = ScoreItemUtils.questionToList(homeworkCommitInfoItem?.correctJson!!,homeworkCommitInfoItem?.correctMode!!)
        ScoreItemUtils.updateAIJsonScores(currentScores, scoreList)

        // 3. 构造并缓存上传参数
        cachedUploadMap = HashMap<String, Any>().apply {
            put("studentTaskId", homeworkCommitInfoItem?.messageId!!)
            put("studentUrl", homeworkCommitInfoItem?.commitUrl!!)
            put("commonTypeId", homeworkCommitInfoItem?.typeId!!)
            put("takeTime", homeworkCommitInfoItem?.takeTime!!)
            put("score", totalScore)
            put("question", gson.toJson(currentScores))
        }

        } catch (e: Exception) {
            stopServiceWithReason("处理AI消息异常")
            return
        }

        // 4. 执行上传
        executeUploadWithRetry()
    }

    /**
     * 执行上传，失败后判断是否重试，达上限则终止服务
     */
    private fun executeUploadWithRetry() {
        val uploadMap = cachedUploadMap ?: run {
            stopServiceWithReason("上传参数为空")
            return
        }
        val url= Constants.URL_BASE+"student/task/pushExamWork"

        val  jsonBody = JSONObject()
        for (key in uploadMap.keys){
            jsonBody.put(key, uploadMap[key])
        }
        Log.d(TAG, "上传参数: ${gson.toJson(cachedUploadMap)}")
        val jsonObjectRequest= object : JsonObjectRequest(Method.POST, url, jsonBody,
            Response.Listener {
                Log.d(TAG, "上传提交结果: $it")
                val code= it.optInt("code")
                val dataStr=it.optString("msg")
                if (it!=null&&code==0){
                    stopServiceWithReason("上传成功")
                }
                else{
                    stopServiceWithReason(dataStr)
                }
            },
            Response.ErrorListener {
                // 判断是否还有重试次数
                if (uploadRetryCount < UPLOAD_MAX_RETRY) {
                    uploadRetryCount++ // 重试次数+1
                    scheduleUploadRetry() // 延迟重试
                } else {
                    stopServiceWithReason("上传失败（达最大重试次数）")
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = SPUtil.getString("token")
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        MyApplication.requestQueue?.add(jsonObjectRequest)
    }

    /**
     * 新增：延迟重试调度方法
     * 功能：通过Handler延迟UPLOAD_RETRY_INTERVAL后，再次执行上传
     */
    private fun scheduleUploadRetry() {
        // 延迟执行重试（使用Handler.postDelayed）
        backgroundHandler.postDelayed({
            Log.d(TAG, "开始第${uploadRetryCount}次重试上传")
            executeUploadWithRetry() // 再次执行上传
        }, UPLOAD_RETRY_INTERVAL)
    }

    /**
     * 原有方法：统一服务终止（保持不变，兜底释放资源）
     */
    private fun stopServiceWithReason(reason: String) {
        Log.d(TAG, "服务终止原因：$reason")
        // 1. 释放重试相关资源
        backgroundHandler.removeCallbacksAndMessages(null) // 移除所有未执行的重试任务
        // 2. 释放原有资源
        aiMessageTimer?.cancel()
        nativeLib = null
        cachedUploadMap = null // 清空上传参数缓存
        // 3. 终止服务
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServiceWithReason("服务被销毁")
    }

}