package com.bll.lnkstudy.utils

import android.util.Log
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.mvp.model.ItemList
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.FileRecorder
import com.qiniu.android.storage.KeyGenerator
import com.qiniu.android.storage.UploadManager
import java.io.File

class FileImageUploadManager(private val uploadToken:String,private val paths:List<String>) {

    private val items= mutableListOf<ItemList>()

    fun startUpload(){
        items.clear()
        Log.d("debug",paths.toString())
        for (i in paths.indices){
            upload(i)
        }
    }


    private fun upload(index:Int) {
        val path=paths[index]
        val recorder = FileRecorder(FileAddress().getPathRecorder())
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
        uploadManager.put(path, (System.currentTimeMillis()/1000+index).toString(), uploadToken,
            { key, info, response ->
                if (info?.isOK == true&&response!=null) {
                    val keyStr=response.optString("key")
                    val downloadUrl="http://cdn.bailianlong.com/${keyStr}?attname=${File(path).name}"
                    items.add(ItemList().apply {
                        id=key.toInt()
                        url=downloadUrl
                    })
                    if (items.size==paths.size){
                        //排序
                        items.sort()
                        val urls= mutableListOf<String>()
                        for (item in items){
                            urls.add(item.url)
                        }
                        Log.d("debug",urls.toString())
                        callBack?.onUploadSuccess(urls)
                    }
                }
                else{
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
        fun onUploadSuccess(urls:List<String>)
        fun onUploadFail()
    }

}