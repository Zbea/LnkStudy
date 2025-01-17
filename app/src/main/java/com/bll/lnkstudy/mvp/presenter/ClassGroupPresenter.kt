package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUserList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager


class ClassGroupPresenter(view: IContractView.IClassGroupView,val screen: Int =0) : BasePresenter<IContractView.IClassGroupView>(view) {

    //加入班群
    fun onInsertClassGroup(classNum:Int) {

        val body=RequestUtils.getBody(
            Pair.create("classGroupId",classNum)
        )
        val insert = RetrofitManager.service.insertGroup(body)
        doRequest(insert, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onInsert()
            }
        }, true)
    }

    //班群信息
    fun onClassGroupInfo(id:Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val quit= RetrofitManager.service.groupInfo(map)
        doRequest(quit, object : Callback<ClassGroup>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ClassGroup>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroup>) {
                view.onClassInfo(tBaseResult.data)
            }
        }, false)
    }

    //退出班群
    fun onQuitClassGroup(id:Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", id)
        )

        val quit= RetrofitManager.service.quitClassGroup(body)
        doRequest(quit, object : Callback<Any>(view,screen) {
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
        doRequest(list, object : Callback<List<ClassGroup>>(view,screen) {
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
    fun getClassGroupUser(map: HashMap<String,Any>) {
        val list= RetrofitManager.service.getClassGroupUser(map)
        doRequest(list, object : Callback<ClassGroupUserList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ClassGroupUserList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroupUserList>) {
                if (tBaseResult.data!=null){
                    view.onUser(tBaseResult.data)
                }
            }
        }, true)
    }

}