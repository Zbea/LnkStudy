package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class SchoolPresenter(view: IContractView.ISchoolView) : BasePresenter<IContractView.ISchoolView>(view) {

    fun getCommonSchool() {
        val grade = RetrofitManager.service.getCommonSchool()
        doRequest(grade, object : Callback<MutableList<SchoolBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<SchoolBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<SchoolBean>>) {
                if (tBaseResult.data!=null)
                    view.onListSchools(tBaseResult.data)
            }
        }, true)

    }

    fun getSchoolDetails(id:Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val editName = RetrofitManager.service.getSchoolDetails(map)
        doRequest(editName, object : Callback<SchoolBean>(view) {
            override fun failed(tBaseResult: BaseResult<SchoolBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<SchoolBean>) {
                if (tBaseResult.data!=null)
                    view.onSchoolDetails(tBaseResult.data)
            }
        }, true)
    }

}