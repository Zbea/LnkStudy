package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager
import okhttp3.MediaType
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
                MediaType.parse("multipart/form-data"), file)))
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


}