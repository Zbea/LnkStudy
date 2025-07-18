package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView,val screen:Int=0) : BasePresenter<IContractView.ICommonView>(view) {

    fun getCommon() {

        val grade = RetrofitManager.service.getCommonData()

        doRequest(grade, object : Callback<CommonData>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonData>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, false)
    }

    fun getCommonSchool() {
        val grade = RetrofitManager.service.getCommonSchool()
        doRequest(grade, object : Callback<MutableList<SchoolBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<SchoolBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<SchoolBean>>) {
                if (tBaseResult.data!=null)
                    view.onListSchools(tBaseResult.data)
            }
        }, true)
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