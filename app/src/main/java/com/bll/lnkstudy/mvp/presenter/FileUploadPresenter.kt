package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FileUploadPresenter(view: IContractView.IFileUploadView):
    BasePresenter<IContractView.IFileUploadView>(view) {

    fun upload(files:List<String>) {

        val parts=ArrayList<MultipartBody.Part>()

        for (url in files){
            val file=File(url)
            parts.add(MultipartBody.Part.createFormData("files", file.name, RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(), file)))
        }

        val type = RetrofitManager.service.upload(parts)
        doRequest(type, object : Callback<List<String>>(view) {
            override fun failed(tBaseResult: BaseResult<List<String>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<String>>) {
                view.onSuccess(tBaseResult.data)
            }
        }, true)
    }

    fun commit(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaper(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }


}