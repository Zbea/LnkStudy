package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.ReceivePaper
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class MainPaperPresenter(view: IContractView.IPaperView): BasePresenter<IContractView.IPaperView>(view) {

    fun getList(map:HashMap<String,Any>) {

        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<ReceivePaper>(view) {
            override fun failed(tBaseResult: BaseResult<ReceivePaper>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ReceivePaper>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }


    fun commitPaper(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaper(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

}