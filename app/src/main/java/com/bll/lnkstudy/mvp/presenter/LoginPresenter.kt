package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class LoginPresenter(view: IContractView.ILoginViewI) : BasePresenter<IContractView.ILoginViewI>(view) {

    fun login(account: String, password: String, timestamp: Int, role: Int) {


        val body = RequestUtils.getBody(

            Pair.create<Any, Any>("account", account),
            Pair.create<Any, Any>("password", password),
            Pair.create<Any, Any>("timestamp", timestamp),
            Pair.create<Any, Any>("role", role)

        )

        val login = RetrofitManager.service.login(body)

        doRequest(login, object : Callback<User>(view) {
            override fun failed(tBaseResult: BaseResult<User>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<User>) {
                view.getLogin(tBaseResult.data)
            }

        }, true)

    }


    fun accounts() {

        val account = RetrofitManager.service.accounts()

        doRequest(account, object : Callback<User>(view) {
            override fun failed(tBaseResult: BaseResult<User>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<User>) {
                view.getAccount(tBaseResult.data)
            }

        }, true)

    }


}