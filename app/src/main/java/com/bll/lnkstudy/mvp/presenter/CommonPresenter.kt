package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.Grade
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView) : BasePresenter<IContractView.ICommonView>(view) {

    fun getGrades() {

        val grade = RetrofitManager.service.getCommonGrade()

        doRequest(grade, object : Callback<MutableList<Grade>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<Grade>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<Grade>>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, false)

    }


}