package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainPresenter(view: IContractView.IMainView) : BasePresenter<IContractView.IMainView>(view) {

    fun getExam(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<PaperList>(view) {
            override fun failed(tBaseResult: BaseResult<PaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperList>) {
                view.onExam(tBaseResult.data)
            }
        }, false)
    }


}