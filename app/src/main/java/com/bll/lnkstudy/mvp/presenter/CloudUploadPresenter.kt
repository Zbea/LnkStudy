package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager

class CloudUploadPresenter(view: IContractView.ICloudUploadView):
    BasePresenter<IContractView.ICloudUploadView>(view) {

    fun upload(list:List<CloudListBean>,isShow:Boolean=false) {
        val body= RequestUtils.getBody(
            Pair.create("listModel",list)
        )
        val type = RetrofitManager.service.cloudUpload(body)
        doRequest(type, object : Callback<MutableList<Int>>(view,0,false) {
            override fun failed(tBaseResult: BaseResult<MutableList<Int>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<Int>>) {
                view.onSuccess(tBaseResult.data)
            }
        }, isShow)
    }

    fun deleteCloud(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteCloudList(body)

        doRequest(delete, object : Callback<Any>(view,0,false) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, false)
    }

}