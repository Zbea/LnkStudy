package com.bll.lnkstudy.utils

import android.util.Log
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.FileRecorder
import com.qiniu.android.storage.KeyGenerator
import com.qiniu.android.storage.UploadManager
import java.io.File

class FileUploadManager(private val uploadToken:String) {

    fun startUpload(targetStr: String, fileName: String){
        autoZip(targetStr,fileName)
    }

    private fun autoZip(targetStr: String, fileName: String) {
        ZipUtils.zip(targetStr, fileName, object : IZipCallback {
            override fun onStart() {
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish() {
                val path = FileAddress().getPathZip(fileName)
                upload(path)
            }
            override fun onError(msg: String?) {
                Log.d("debug","onError ${fileName}:$msg")
            }
        })
    }

    private fun upload(path: String) {
        val dirPath = Constants.RECORDER_PATH
        val recorder = FileRecorder(dirPath)
        //默认使用 key 的 url_safe_base64 编码字符串作为断点记录文件的文件名
        //避免记录文件冲突（特别是 key 指定为 null 时），也可自定义文件名(下方为默认实现)：
        val keyGen = object : KeyGenerator {
            override fun gen(key: String?, file: File?): String {
                return key + "_._" + StringBuffer(file!!.absolutePath).reverse()
            }

            override fun gen(key: String?, sourceId: String?): String {
                return key + "_._" + StringBuffer(File(sourceId).absolutePath).reverse()
            }
        }

        val config = Configuration.Builder()
            .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2) // 使用新版分片上传
            .useConcurrentResumeUpload(true) // 开启并发上传，默认为 NO
            .concurrentTaskCount(3) // 并发上传线程数量为3
            .recorder(recorder, keyGen) // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            .build()

        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        val uploadManager = UploadManager(config)
        uploadManager.put(path, null, uploadToken,
            { key, info, response ->
                if (info?.isOK == true) {
                    FileUtils.deleteFile(File(path))
                    val keyStr=response.optString("key")
                    val downloadUrl="http://cdn.bailianlong.com/${keyStr}?attname=${File(path).name}"
                    Log.d("debug",downloadUrl)
                    callBack?.onUpLoadSuccess(downloadUrl)
                }
            }, null
        )

    }

    private var callBack: UpLoadCallBack? = null

    fun setCallBack(callBack: UpLoadCallBack) {
        this.callBack = callBack
    }

    fun interface UpLoadCallBack {
        fun onUpLoadSuccess(url:String)
    }

}