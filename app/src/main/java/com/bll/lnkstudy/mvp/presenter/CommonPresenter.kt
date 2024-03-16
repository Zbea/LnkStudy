package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView,val screen:Int=0) : BasePresenter<IContractView.ICommonView>(view) {

    fun getCommonData() {

        val grade = RetrofitManager.service.getCommonData()

        doRequest(grade, object : Callback<CommonData>(view,screen) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonData>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, false)
    }


}