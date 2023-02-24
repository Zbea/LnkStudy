package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.CommonBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView) : BasePresenter<IContractView.ICommonView>(view) {

    fun getGrades() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<CommonBean>(view) {
            override fun failed(tBaseResult: BaseResult<CommonBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonBean>) {
                view.onList(tBaseResult.data?.grades)
            }

        }, false)

    }



}