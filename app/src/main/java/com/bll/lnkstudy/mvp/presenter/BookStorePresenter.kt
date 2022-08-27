package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.BookEvent
import com.bll.lnkstudy.mvp.model.BookStore
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class BookStorePresenter(view: IContractView.IBookStoreView) : BasePresenter<IContractView.IBookStoreView>(view) {

    fun getBooks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getBooks(map)

        doRequest(books, object : Callback<BookStore>(view) {
            override fun failed(tBaseResult: BaseResult<BookStore>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStore>) {
                view.onBookStore(tBaseResult.data)
            }

        }, true)

    }

    fun buyBook(id:String) {

        val books = RetrofitManager.service.buyBook(id)

        doRequest(books, object : Callback<BookEvent>(view) {
            override fun failed(tBaseResult: BaseResult<BookEvent>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookEvent>) {
                view.onBuyBook(tBaseResult.data)
            }

        }, true)

    }

    fun downBook(id:String) {

        val books = RetrofitManager.service.downloadBook(id)

        doRequest(books, object : Callback<BookEvent>(view) {
            override fun failed(tBaseResult: BaseResult<BookEvent>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookEvent>) {
                view.onDownBook(tBaseResult.data)
            }

        }, true)

    }

}