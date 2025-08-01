package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager


class SmsPresenter(view: IContractView.ISmsView, val screen:Int=1) : BasePresenter<IContractView.ISmsView>(view) {

    fun sms(phone:String) {
        val sms = RetrofitManager.service.getSms(phone)
        doRequest(sms, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSms()
            }
        }, true)
    }

    fun checkPhone(code: String) {
        val body = RequestUtils.getBody(
            Pair.create("code", code)
        )
        val editName = RetrofitManager.service.checkPhone(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCheckSuccess()
            }
        }, true)
    }

}