package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

/**
 * 应用相关
 */
class DownloadAppPresenter(view: IContractView.IAPPView) : BasePresenter<IContractView.IAPPView>(view) {

    fun getAppList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getApks(map)

        doRequest(app, object : Callback<AppList>(view) {
            override fun failed(tBaseResult: BaseResult<AppList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppList>) {
                view.onAppList(tBaseResult.data)
            }

        }, true)
    }


    fun buyApk(map: HashMap<String, Any> ) {

        val requestBody=RequestUtils.getBody(map)
        val download = RetrofitManager.service.buyApk(requestBody)

        doRequest(download, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }

        }, true)

    }

}