package com.bll.lnkstudy.utils

import android.util.Log
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.google.gson.Gson
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.FileRecorder
import com.qiniu.android.storage.KeyGenerator
import com.qiniu.android.storage.UploadManager
import java.io.File

class FileUploadManager(private val uploadToken:String) {

    fun startUpload(filePath: String){
        Log.d(Constants.DEBUG,filePath)
        upload(filePath,1)
    }

    fun startZipUpload(filePath: String, fileName: String){
        Log.d(Constants.DEBUG,filePath)
        autoZip(filePath,fileName)
    }
    fun startZipUpload(targetPaths: List<String>, fileName: String){
        Log.d(Constants.DEBUG, Gson().toJson(targetPaths))
        autoZip(targetPaths,fileName)
    }

    private fun autoZip(filePath: String, fileName: String) {
        ZipUtils.zip(filePath, fileName, object : IZipCallback {
            override fun onStart() {
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish() {
                val path = FileAddress().getPathZip(fileName)
                upload(path,0)
            }
            override fun onError(msg: String?) {
                Log.d(Constants.DEBUG,"onError ${fileName}:$msg")
                callBack?.onUploadFail()
            }
        })
    }

    private fun autoZip(targetPaths: List<String>, fileName: String) {
        ZipUtils.zip(targetPaths, fileName, object : IZipCallback {
            override fun onStart() {
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish() {
                val path = FileAddress().getPathZip(fileName)
                upload(path,0)
            }
            override fun onError(msg: String?) {
                Log.d("debug","onError ${fileName}:$msg")
                callBack?.onUploadFail()
            }
        })
    }

    /**
     * type==0上传zip type==1直接上传文件
     */
    private fun upload(path: String,type:Int) {
        val recorder = FileRecorder(FileAddress().getPathRecorder())
        //默认使用 key 的 url_safe_base64 编码字符串作为断点记录文件的文件名
        //避免记录文件冲突（特别是 key 指定为 null 时），也可自定义文件名(下方为默认实现)：
        val keyGen = object : KeyGenerator {
            override fun gen(key: String?, file: File?): String {
                return key + "_${ToolUtils.getOtaSerialNumber()}._" + StringBuffer(file!!.absolutePath).reverse()
            }

            override fun gen(key: String?, sourceId: String?): String {
                return key + "_${ToolUtils.getOtaSerialNumber()}._" + StringBuffer(File(sourceId).absolutePath).reverse()
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
                if (info?.isOK == true&& response != null) {
                    if (type==0){
                        FileUtils.deleteFile(File(path))
                    }
                    val keyStr=response.optString("key")
                    val downloadUrl="${Constants.UPDATE_URL}${keyStr}?attname=${File(path).name}"
                    Log.d(Constants.DEBUG,downloadUrl)
                    callBack?.onUploadSuccess(downloadUrl)
                }
                else{
                    Log.d(Constants.DEBUG,info.toString())
                    callBack?.onUploadFail()
                }
            }, null
        )

    }

    private var callBack: UploadCallBack? = null

    fun setCallBack(callBack: UploadCallBack) {
        this.callBack = callBack
    }

    interface UploadCallBack {
        fun onUploadSuccess(url:String)
        fun onUploadFail()
    }
}