package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppListBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class AppPresenter(view: IContractView.IAPPView) : BasePresenter<IContractView.IAPPView>(view) {

    fun getAppList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getApks(map)

        doRequest(app, object : Callback<AppListBean>(view) {
            override fun failed(tBaseResult: BaseResult<AppListBean>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppListBean>) {
                view.onAppList(tBaseResult.data)
            }

        }, true)
    }


    fun download(id:String) {

        val download = RetrofitManager.service.downloadApk(id)

        doRequest(download, object : Callback<AppListBean>(view) {
            override fun failed(tBaseResult: BaseResult<AppListBean>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppListBean>) {
                view.onDown(tBaseResult.data)
            }

        }, true)

    }

}