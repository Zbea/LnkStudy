package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.book.TextbookStore
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager


class TextbookStorePresenter(view: IContractView.ITextbookStoreView, val screen: Int =0) : BasePresenter<IContractView.ITextbookStoreView>(view) {

    /**
     * 题卷本
     */
    fun getHomeworkBooks(map: HashMap<String,Any>) {
        val books = RetrofitManager.service.getHomeworkBooks(map)
        doRequest(books, object : Callback<TextbookStore>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TextbookStore>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TextbookStore>) {
                if (tBaseResult.data!=null)
                    view.onTextbook(tBaseResult.data)
            }
        }, true)
    }

    /**
     * 教材
     */
    fun getTextBooks(map: HashMap<String,Any>) {
        val books = RetrofitManager.service.getTextBooks(map)
        doRequest(books, object : Callback<TextbookStore>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TextbookStore>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TextbookStore>) {
                view.onTextbook(tBaseResult.data)
            }
        }, true)
    }

    fun buyBook(map: HashMap<String,Any>){
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