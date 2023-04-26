package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class CloudUploadPresenter(view: IContractView.ICloudUploadView):
    BasePresenter<IContractView.ICloudUploadView>(view) {

    fun upload(list:List<CloudListBean>) {
        val body= RequestUtils.getBody(
            Pair.create("listModel",list)
        )
        val type = RetrofitManager.service.cloudUpload(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, false)
    }

}