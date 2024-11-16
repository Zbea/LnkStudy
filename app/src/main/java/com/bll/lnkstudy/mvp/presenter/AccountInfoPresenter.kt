package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager


class AccountInfoPresenter(view: IContractView.IAccountInfoView,val screen:Int) : BasePresenter<IContractView.IAccountInfoView>(view) {

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

    fun editBirthday(time: Long) {
        val body = RequestUtils.getBody(
            Pair.create("birthdayTime", time)
        )
        val editName = RetrofitManager.service.editBirthday(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditBirthday()
            }
        }, true)
    }

    fun editPhone(code: String,phone: String) {
        val body = RequestUtils.getBody(
            Pair.create("telNumber", phone),
            Pair.create("code", code)
        )
        val editName = RetrofitManager.service.editPhone(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditPhone()
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



    fun editName(name: String) {
        val body = RequestUtils.getBody(
            Pair.create("nickName", name)
        )
        val editName = RetrofitManager.service.editName(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditNameSuccess()
            }

        }, true)
    }

    fun editSchool(id: Int) {
        val map=HashMap<String,Any>()
        map["schoolId"]=id
        val body = RequestUtils.getBody(map)
        val editName = RetrofitManager.service.editSchool(body)

        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSchool()
            }
        }, true)
    }

    fun editParent(name: String,nickname:String,phone:String) {
        val map=HashMap<String,Any>()
        map["parentName"]=name
        map["parentNickname"]=nickname
        map["parentTel"]=phone
        val body = RequestUtils.getBody(map)
        val editName = RetrofitManager.service.editParent(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditParent()
            }
        }, true)
    }

    fun logout() {
        val logout = RetrofitManager.service.logout()
        doRequest(logout, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onLogout()
            }
        }, true)

    }


}