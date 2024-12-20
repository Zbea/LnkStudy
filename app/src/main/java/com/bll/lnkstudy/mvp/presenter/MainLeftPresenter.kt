package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainLeftPresenter(view: IContractView.IMainLeftView, val screen: Int=0) : BasePresenter<IContractView.IMainLeftView>(view) {

    //获取更新信息
    fun getAppUpdate() {
        val list= RetrofitManager.service.onAppUpdate()
        doRequest(list, object : Callback<AppUpdateBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<AppUpdateBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AppUpdateBean>) {
                if (tBaseResult.data!=null)
                    view.onAppUpdate(tBaseResult.data)
            }
        }, false)
    }

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

    fun getSchoolPermission() {
        val type = RetrofitManager.service.getPermissionSchoolAllow()
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

}