package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class AppPresenter(view: IContractView.IAPPViewI) : BasePresenter<IContractView.IAPPViewI>(view) {

    fun getAppList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getApks(map)

        doRequest(app, object : Callback<AppBean>(view) {
            override fun failed(tBaseResult: BaseResult<AppBean>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppBean>) {
                view.onAppList(tBaseResult.data)
            }

        }, true)
    }


    fun download(id:String) {

        val download = RetrofitManager.service.downloadApk(id)

        doRequest(download, object : Callback<AppBean>(view) {
            override fun failed(tBaseResult: BaseResult<AppBean>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppBean>) {
                view.onDownBook(tBaseResult.data)
            }

        }, true)

    }

}