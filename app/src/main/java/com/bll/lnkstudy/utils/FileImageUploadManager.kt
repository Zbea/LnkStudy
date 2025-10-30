package com.bll.lnkstudy.utils

import android.util.Log
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.google.gson.Gson
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.FileRecorder
import com.qiniu.android.storage.KeyGenerator
import com.qiniu.android.storage.UploadManager
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FileImageUploadManager(private val uploadToken: String, private val paths: List<String>) {
    // 存储上传结果（index -> url），保证原始顺序
    private val resultMap = ConcurrentHashMap<Int, String>()

    // 已完成的任务数（成功+失败）
    private val completedCount = AtomicInteger(0)

    // 是否已失败（原子类保证线程安全）
    private val isFailed = AtomicBoolean(false)

    // 复用UploadManager，避免重复创建
    private val uploadManager: UploadManager

    init {
        // 初始化UploadManager（全局单例复用）
        val recorder = FileRecorder(FileAddress().getPathRecorder())
        val keyGen = object : KeyGenerator {
            override fun gen(key: String?, file: File?): String {
                return key + "_${ToolUtils.getOtaSerialNumber()}._" + StringBuffer(file!!.absolutePath).reverse()
            }

            override fun gen(key: String?, sourceId: String?): String {
                return key + "_${ToolUtils.getOtaSerialNumber()}._" + StringBuffer(File(sourceId).absolutePath).reverse()
            }
        }
        val config = Configuration.Builder()
            .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2)
            .useConcurrentResumeUpload(true)
            .concurrentTaskCount(3)
            .recorder(recorder, keyGen)
            .build()
        uploadManager = UploadManager(config)
    }

    fun startUpload() {
        // 重置状态（支持重复调用）
        resultMap.clear()
        completedCount.set(0)
        isFailed.set(false)

        if (paths.isEmpty()) {
            uploadFail("地址为空")
            return
        }

        Log.d(Constants.DEBUG, Gson().toJson(paths))

        for (path in paths){
            if (!FileUtils.isExist(path)){
                uploadFail(path+"不存在")
                return
            }
        }
        // 启动所有上传任务，并保存任务引用
        for (i in paths.indices) {
            UploadTask(i).start()
        }
    }

    // 封装上传任务，便于管理和取消
    private inner class UploadTask(private val index: Int) {
        fun start() {
            val path = paths[index]
            val key = "${ToolUtils.getOtaSerialNumber()}_${System.currentTimeMillis()}_$index"

            uploadManager.put(path, key, uploadToken,
                { _, info, response ->
                    handleUploadResult(index, path, info, response)
                },
                null, // 进度回调（可选）
            )
        }
    }

    // 处理单个任务的上传结果
    private fun handleUploadResult(index: Int, path: String, info: ResponseInfo?, response: JSONObject?) {
        // 若已失败，直接返回
        if (isFailed.get()) return
        // 标记任务完成
        val currentCompleted = completedCount.incrementAndGet()

        if (info?.isOK == true && response != null) {
            // 上传成功：按原始index存入resultMap
            val keyStr = response.optString("key")
            val downloadUrl = "${Constants.UPDATE_URL}$keyStr?attname=${File(path).name}"
            resultMap[index] = downloadUrl

            // 检查是否所有任务都完成
            if (currentCompleted == paths.size) {
                val sortedUrls = List(paths.size) { resultMap[it] ?: "" }
                callBack?.onUploadSuccess(sortedUrls)
                Log.d(Constants.DEBUG, Gson().toJson(sortedUrls))
            }
        } else {
            val errorMsg = path+"/"+(info?.toString() ?: "未知上传错误")
            uploadFail(errorMsg)
        }
    }

    private fun uploadFail(string: String){
        Log.d(Constants.DEBUG, "失败原因：$string")
        // 上传失败：标记失败状态，取消所有未完成任务
        isFailed.set(true)
        callBack?.onUploadFail()
    }

    // 外部可调用此方法取消所有上传
    fun cancelAll() {
        isFailed.set(true)
    }

    private var callBack: UploadCallBack? = null

    fun setCallBack(callBack: UploadCallBack) {
        this.callBack = callBack
    }

    interface UploadCallBack {
        fun onUploadSuccess(urls: List<String>)
        fun onUploadFail()
    }
}