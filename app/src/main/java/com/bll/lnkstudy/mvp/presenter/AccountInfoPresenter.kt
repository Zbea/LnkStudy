package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class AccountInfoPresenter(view: IContractView.IAccountInfoView,val screen:Int) : BasePresenter<IContractView.IAccountInfoView>(view) {

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

    fun editGrade(grade: Int) {

        val body = RequestUtils.getBody(
            Pair.create("grade", grade)
        )

        val editName = RetrofitManager.service.editGrade(body)

        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditGradeSuccess()
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