package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.SystemUpdateInfo
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager
import com.bll.lnkstudy.net.system.BasePresenter1
import com.bll.lnkstudy.net.system.BaseResult1
import com.bll.lnkstudy.net.system.Callback1


class SystemManagerPresenter(view: IContractView.ISystemView, val screen: Int) : BasePresenter1<IContractView.ISystemView>(view) {

    fun checkSystemUpdate(map: Map<String,String>) {

        val body = RequestUtils.getBody(map)

        val request = RetrofitManager.service1.RELEASE_CHECK_UPDATE(body)
        doRequest(request, object : Callback1<SystemUpdateInfo>(view, screen,false) {
            override fun failed(tBaseResult: BaseResult1<SystemUpdateInfo>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult1<SystemUpdateInfo>) {
                if (tBaseResult.Data!=null)
                    view.onUpdateInfo(tBaseResult.Data)
            }
        }, false)
    }
}