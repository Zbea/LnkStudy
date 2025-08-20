package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.book.TextbookStore
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager


class DownloadDictionaryPresenter(view: IContractView.IDictionaryResourceView, val screen: Int =0) : BasePresenter<IContractView.IDictionaryResourceView>(view) {

    fun getList(map: HashMap<String,Any>) {
        val books = RetrofitManager.service.getTextBooks(map)
        doRequest(books, object : Callback<TextbookStore>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TextbookStore>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TextbookStore>) {
                view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun buyDictionary(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val buy = RetrofitManager.service.buy(body)
        doRequest(buy, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }
        }, true)
    }

}