package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.AccountQdBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class WalletPresenter(view: IContractView.IWalletView,val screen:Int=0) : BasePresenter<IContractView.IWalletView>(view) {

    //获取学豆列表
    fun getXdList(boolean: Boolean) {
        val list = RetrofitManager.service.getSMoneyList()
        doRequest(list, object : Callback<MutableList<AccountQdBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<AccountQdBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<AccountQdBean>>) {
                view.onXdList(tBaseResult.data)
            }

        }, boolean)

    }

    //提交学豆订单
    fun postXdOrder(id:String,payType:Int)
    {
        val post = RetrofitManager.service.postOrder(id,payType)
        doRequest(post, object : Callback<AccountOrder>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.onXdOrder(tBaseResult.data)
            }
        }, true)
    }

    //查看订单状态
    fun checkOrder(id:String)
    {
        val order = RetrofitManager.service.getOrderStatus(id)
        doRequest(order, object : Callback<AccountOrder>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.checkOrder(tBaseResult.data)
            }
        }, false)
    }


    fun accounts() {

        val account = RetrofitManager.service.accounts()

        doRequest(account, object : Callback<User>(view,screen) {
            override fun failed(tBaseResult: BaseResult<User>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<User>) {
                view.getAccount(tBaseResult.data)
            }

        }, true)

    }


}