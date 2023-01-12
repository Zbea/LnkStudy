package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class AccountInfoPresenter(view: IContractView.IAccountInfoView) : BasePresenter<IContractView.IAccountInfoView>(view) {


    //获取vip列表
    fun getVipList(boolean: Boolean) {
        var map=HashMap<String,String>()
        map.put("pageIndex", "1")
        map.put("pageSize", "10")
        val list = RetrofitManager.service.getVipList(map)
        doRequest(list, object : Callback<AccountList>(view) {
            override fun failed(tBaseResult: BaseResult<AccountList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountList>) {
                view.getVipList(tBaseResult.data)
            }
        }, boolean)
    }

    //提交Vip订单
    fun postVip(id:String)
    {
        val post = RetrofitManager.service.postOrderVip(id)
        doRequest(post, object : Callback<AccountOrder>(view) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.onVipOrder(tBaseResult.data)
            }
        }, true)
    }


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