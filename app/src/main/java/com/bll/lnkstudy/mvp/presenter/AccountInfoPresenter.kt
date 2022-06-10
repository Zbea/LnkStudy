package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class AccountInfoPresenter(view: IContractView.IAccountInfoViewI) : BasePresenter<IContractView.IAccountInfoViewI>(view) {

    //获取学豆列表
    fun getXdList() {

        var map=HashMap<String,String>()
        map.put("pageIndex", "1")
        map.put("pageSize", "10")

        val list = RetrofitManager.service.getSMoneyList(map)
        doRequest(list, object : Callback<AccountList>(view) {
            override fun failed(tBaseResult: BaseResult<AccountList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountList>) {
                view.getXdList(tBaseResult.data)
            }

        }, true)

    }

    //获取vip列表
    fun getVipList() {
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
        }, true)
    }

    //提交学豆订单
    fun postXdOrder(id:String)
    {
        val post = RetrofitManager.service.postOrder(id)
        doRequest(post, object : Callback<AccountOrder>(view) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.onXdOrder(tBaseResult.data)
            }
        }, true)
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

    //查看订单状态
    fun checkOrder(id:String)
    {
        val order = RetrofitManager.service.getOrderStatus(id)
        doRequest(order, object : Callback<AccountOrder>(view) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.checkOrder(tBaseResult.data)
            }
        }, false)
    }

    fun editName(name: String) {

        val body = RequestUtils.getBody(
            Pair.create<Any, Any>("nickName", name)
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