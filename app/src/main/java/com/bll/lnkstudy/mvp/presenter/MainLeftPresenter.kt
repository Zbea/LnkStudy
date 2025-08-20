package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainLeftPresenter(view: IContractView.IMainLeftView, val screen: Int=0) : BasePresenter<IContractView.IMainLeftView>(view) {

    fun active() {
        val type = RetrofitManager.service.active()
        doRequest(type, object : Callback<Any>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }

    fun getParentPermission() {
        val type = RetrofitManager.service.getPermissionParentAllow()
        doRequest(type, object : Callback<PermissionParentBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<PermissionParentBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PermissionParentBean>) {
                if (tBaseResult.data!=null)
                    view.onParentPermission(tBaseResult.data)
            }
        }, false)
    }

    fun getSchoolPermission(grade:Int) {
        val map=HashMap<String,Any>()
        map["grade"]=grade
        val type = RetrofitManager.service.getPermissionSchoolAllow(map)
        doRequest(type, object : Callback<PermissionSchoolBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<PermissionSchoolBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PermissionSchoolBean>) {
                if (tBaseResult.data!=null)
                    view.onSchoolPermission(tBaseResult.data)
            }
        }, false)
    }

    fun getClassGroupPermission() {
        val grade = RetrofitManager.service.getClassGroupPermission()
        doRequest(grade, object : Callback<Long>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<Long>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Long>) {
                if (tBaseResult.data!=null)
                    view.onClassGroupPermission(tBaseResult.data!!)
            }

        }, false)
    }

}