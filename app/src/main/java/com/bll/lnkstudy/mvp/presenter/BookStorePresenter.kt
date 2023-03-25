package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.BookStore
import com.bll.lnkstudy.mvp.model.BookStoreType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class BookStorePresenter(view: IContractView.IBookStoreView) : BasePresenter<IContractView.IBookStoreView>(view) {

    /**
     * 书籍
     */
    fun getBooks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getBooks(map)

        doRequest(books, object : Callback<BookStore>(view) {
            override fun failed(tBaseResult: BaseResult<BookStore>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStore>) {
                if (tBaseResult.data!=null)
                    view.onBook(tBaseResult.data)
            }

        }, true)

    }

    /**
     * 教材
     */
    fun getTextBooks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getTextBooks(map)

        doRequest(books, object : Callback<BookStore>(view) {
            override fun failed(tBaseResult: BaseResult<BookStore>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStore>) {
                view.onBook(tBaseResult.data)
            }

        }, true)

    }

    /**
     * 参考教材
     */
    fun getTextBookCks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getTextBookCKs(map)

        doRequest(books, object : Callback<BookStore>(view) {
            override fun failed(tBaseResult: BaseResult<BookStore>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStore>) {
                view.onBook(tBaseResult.data)
            }

        }, true)

    }

    /**
     * 获取分类
     */
    fun getBookType() {

        val type = RetrofitManager.service.getBookType()

        doRequest(type, object : Callback<BookStoreType>(view) {
            override fun failed(tBaseResult: BaseResult<BookStoreType>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStoreType>) {
                if (tBaseResult.data!=null)
                    view.onType(tBaseResult.data)
            }

        }, true)

    }

    fun buyBook(map: HashMap<String,Any>){

        val body=RequestUtils.getBody(map)
        val buy = RetrofitManager.service.buyBooks(body)

        doRequest(buy, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buyBookSuccess()
            }

        }, true)
    }

}