package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class RegisterOrFindPsdPresenter(view: IContractView.IRegisterOrFindPsdViewI) : BasePresenter<IContractView.IRegisterOrFindPsdViewI>(view) {

    fun register(role: String,account: String, password: String, name: String,phone: String,code: String) {


        val body = RequestUtils.getBody(

            Pair.create<Any, Any>("account", account),
            Pair.create<Any, Any>("password", password),
            Pair.create<Any, Any>("nickname", name),
            Pair.create<Any, Any>("role", role),
            Pair.create<Any, Any>("smsCode", code),
            Pair.create<Any, Any>("telNumber", phone)

        )

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

            Pair.create<Any, Any>("account", account),
            Pair.create<Any, Any>("password", psd),
            Pair.create<Any, Any>("role", role),
            Pair.create<Any, Any>("smsCode", code),
            Pair.create<Any, Any>("telNumber", phone)

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

            Pair.create<Any, Any>("password", psd),
            Pair.create<Any, Any>("smsCode", code),
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