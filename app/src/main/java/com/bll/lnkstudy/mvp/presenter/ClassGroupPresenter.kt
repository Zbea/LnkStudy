package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class ClassGroupPresenter(view: IContractView.IClassGroupView) : BasePresenter<IContractView.IClassGroupView>(view) {

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
                if (tBaseResult.data!=null)
                    view.onClassGroupList(tBaseResult.data)
            }
        }, boolean)
    }

    //获取同学列表
    fun getClassGroupUser() {
        val list= RetrofitManager.service.getClassGroupUser()
        doRequest(list, object : Callback<List<ClassGroupUser>>(view) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroupUser>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroupUser>>) {
                if (!tBaseResult.data.isNullOrEmpty()){
                    view.onUser(tBaseResult.data)
                }
            }
        }, true)
    }

}