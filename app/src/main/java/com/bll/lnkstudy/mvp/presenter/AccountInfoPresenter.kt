package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class AccountInfoPresenter(view: IContractView.IAccountInfoView) : BasePresenter<IContractView.IAccountInfoView>(view) {

    fun editName(name: String) {

        val body = RequestUtils.getBody(
            Pair.create("nickName", name)
        )

        val editName = RetrofitManager.service.editName(body)

        doRequest(editName, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditNameSuccess()
            }

        }, true)

    }

    fun logout() {

        val logout = RetrofitManager.service.logout()

        doRequest(logout, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onLogout()
            }

        }, true)

    }


}