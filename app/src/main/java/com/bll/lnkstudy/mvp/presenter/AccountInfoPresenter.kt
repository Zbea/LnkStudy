package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.ClassGroup
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


    //加入班群
    fun onInsertClassGroup(classNum:Int) {

        val body=RequestUtils.getBody(
            Pair.create("classNum",classNum)
        )
        val insert = RetrofitManager.service.insertGroup(body)
        doRequest(insert, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onInsert()
            }
        }, true)
    }

    //退出班群
    fun onQuitClassGroup(id:Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", id)
        )

        val quit= RetrofitManager.service.quitClassGroup(body)
        doRequest(quit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onQuit()
            }
        }, true)
    }

    //班群列表
    fun getClassGroupList(boolean: Boolean) {
        val list= RetrofitManager.service.groupList()
        doRequest(list, object : Callback<List<ClassGroup>>(view) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroup>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroup>>) {
                if (!tBaseResult?.data.isNullOrEmpty()){
                    view.onClassGroupList(tBaseResult.data as List<ClassGroup>)
                }
            }
        }, boolean)
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