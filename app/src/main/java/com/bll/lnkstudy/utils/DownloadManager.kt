package com.bll.lnkstudy.utils

import android.content.Context
import android.text.TextUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadLargeFileListener
import com.liulishuo.filedownloader.FileDownloader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class DownloadManager(private val context: Context) {
    // 当前页面的所有任务（线程安全列表）
    private val taskList = CopyOnWriteArrayList<BaseDownloadTask>()

    // 单任务下载回调
    interface SingleCallback {
        fun onProgress(taskId: Int, soFar: Long, total: Long)
        fun onCompleted(taskId: Int)
        fun onPaused(taskId: Int, soFar: Long, total: Long) {}
        fun onFailed(taskId: Int, error: String)
    }

    // 多任务下载回调
    interface BatchCallback {
        fun onSingleProgress(taskId: Int, url: String, soFar: Long, total: Long) {}
        fun onSingleCompleted(taskId: Int, url: String, savePath: String) {}
        fun onBatchCompleted()
        fun onBatchFailed(error: String)
    }

    // -------------------------- 单任务下载（传入单个URL和路径） --------------------------
    /**
     * 启动单任务下载
     * @param url 单个下载URL
     * @param savePath 单个保存路径（可选，默认自动生成）
     * @param isLargeFile 是否大文件（默认根据URL后缀判断，也可手动指定）
     */
    fun startSingle(url: String, savePath: String, isLargeFile: Boolean = isLargeFileByUrl(url), callback: SingleCallback) {
        // 校验URL
        if (TextUtils.isEmpty(url)) {
            callback.onFailed(-1, "URL不能为空")
            return
        }
        if (TextUtils.isEmpty(savePath)) {
            callback.onFailed(-1, "保存地址不能为空")
            return
        }
        // 创建任务
        val task = createTask(url, savePath, isLargeFile)
        task.setListener(object : FileDownloadLargeFileListener() {
            override fun pending(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            }
            override fun progress(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
                callback.onProgress(task.id, soFarBytes, totalBytes)
            }
            override fun completed(task: BaseDownloadTask) {
                callback.onCompleted(task.id)
                removeTask(task.id) // 完成后移除任务
            }
            override fun paused(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
                callback.onPaused(task.id, soFarBytes, totalBytes)
            }
            override fun error(task: BaseDownloadTask, e: Throwable) {
                callback.onFailed(task.id, e.message.toString())
                FileUtils.delete(task.path)
                removeTask(task.id) // 失败后移除任务
            }
            override fun warn(task: BaseDownloadTask) {
            }
        })

        // 启动并记录任务
        task.start()
        taskList.add(task)
    }

    // -------------------------- 多任务下载（传入多个URL和路径） --------------------------
    /**
     * 启动多任务下载
     * @param urlList 多个下载URL（与savePathList一一对应）
     * @param savePathList 多个保存路径（可选，长度需与urlList一致，默认自动生成）
     * @param isLargeFiles 是否大文件列表（默认根据URL后缀判断）
     */
    fun startBatch(urlList: List<String>, savePathList: List<String>, isLargeFiles: List<Boolean> = urlList.map { isLargeFileByUrl(it) }, callback: BatchCallback) {
        // 校验参数长度
        if (urlList.isEmpty()) {
            callback.onBatchFailed("URL不能为空")
            return
        }
        if (savePathList.isEmpty()) {
            callback.onBatchFailed("保存地址不能为空")
            return
        }
        if (urlList.size != savePathList.size) {
            callback.onBatchFailed("URL列表与保存地址长度不一致")
            throw IllegalArgumentException()
        }
        val activeCount = AtomicInteger(0)
        activeCount.addAndGet(urlList.size)

        val currentTask= mutableListOf<BaseDownloadTask>()
        // 逐个启动子任务
        urlList.forEachIndexed { index, url ->
            val savePath = (savePathList[index])
            val isLarge = if (index < isLargeFiles.size) isLargeFiles[index] else false
            // 创建子任务
            val task = createTask(url, savePath, isLarge)
            task.setListener(object : FileDownloadLargeFileListener() {
                override fun progress(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
                    callback.onSingleProgress(task.id, url, soFarBytes, totalBytes)
                }
                override fun completed(task: BaseDownloadTask) {
                    callback.onSingleCompleted(task.id, url, savePath)
                    if (activeCount.decrementAndGet() == 0) {
                        callback.onBatchCompleted()
                        currentTask.forEach { task ->
                            removeTask(task.id)
                        }
                        currentTask.clear()
                    }
                }
                override fun error(task: BaseDownloadTask, e: Throwable) {
                    callback.onBatchFailed(e.message.toString())
                    currentTask.forEach { task ->
                        FileDownloader.getImpl().pause(task.id)
                        // 清理未完成的临时文件
                        FileUtils.delete(task.path)
                        removeTask(task.id)
                    }
                    currentTask.clear()
                }
                // 其他回调方法（pending/paused等）按需实现
                override fun pending(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {}
                override fun paused(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {}
                override fun warn(task: BaseDownloadTask) {}
            })
            // 启动并记录任务
            task.start()
            currentTask.add(task)
            taskList.add(task)
        }
    }

    // -------------------------- 任务控制（暂停/取消/清理） --------------------------
    /** 暂停当前页面的所有任务 */
    fun pauseAll() {
        taskList.forEach { task ->
            FileDownloader.getImpl().pause(task.id)
            // 清理未完成的临时文件
            FileUtils.delete(task.path)
        }
        taskList.clear()
    }

    // -------------------------- 工具方法 --------------------------
    /** 创建下载任务（区分大文件） */
    private fun createTask(url: String, savePath: String, isLargeFile: Boolean): BaseDownloadTask {
        return FileDownloader.getImpl().create(url).apply {
            path = savePath
            autoRetryTimes = 2
            isForceReDownload = !isLargeFile//大文件支持断点续传
            addHeader("Accept-Encoding", "identity")
            addHeader("Authorization", SPUtil.getString("token"))
        }
    }

    /** 自动判断是否为大文件（根据常见大文件后缀） */
    private fun isLargeFileByUrl(url: String): Boolean {
        val largeSuffixes = listOf(".zip", ".rar", ".apk", ".mp4", ".iso", ".tar", ".gz",".pdf",".ppt",".pptx")
        return largeSuffixes.any { url.endsWith(it, ignoreCase = true) }
    }

    /** 移除已完成/失败的任务 */
    private fun removeTask(taskId: Int) {
        taskList.removeAll { it.id == taskId }
    }

}
