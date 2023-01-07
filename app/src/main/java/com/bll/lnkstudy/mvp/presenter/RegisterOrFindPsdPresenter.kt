package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class RegisterOrFindPsdPresenter(view: IContractView.IRegisterOrFindPsdView) : BasePresenter<IContractView.IRegisterOrFindPsdView>(view) {

    fun register(map:HashMap<String,Any>) {

        val body = RequestUtils.getBody(map)

        val register = RetrofitManager.service.register(body)

        doRequest(register, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onRegister()
            }

        }, true)

    }

    fun findPsd(role: String,account: String, psd: String,phone: String,code: String) {


        val body = RequestUtils.getBody(

            Pair.create("account", account),
            Pair.create("password", psd),
            Pair.create("role", role),
            Pair.create("smsCode", code),
            Pair.create("telNumber", phone)

        )

        val findpsd = RetrofitManager.service.findPassword(body)

        doRequest(findpsd, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onFindPsd()
            }

        }, true)

    }


    fun editPsd(psd: String,code: String) {


        val body = RequestUtils.getBody(

            Pair.create("password", psd),
            Pair.create("smsCode", code),
        )

        val findpsd = RetrofitManager.service.editPassword(body)

        doRequest(findpsd, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditPsd()
            }

        }, true)

    }

    fun sms(phone:String) {

        val sms = RetrofitManager.service.getSms(phone)

        doRequest(sms, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSms()
            }

        }, true)

    }


}

