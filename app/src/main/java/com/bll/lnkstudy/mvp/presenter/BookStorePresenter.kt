package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.book.BookStore
import com.bll.lnkstudy.mvp.model.book.BookStoreType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class BookStorePresenter(view: IContractView.IBookStoreView, val screen: Int =0) : BasePresenter<IContractView.IBookStoreView>(view) {

    /**
     * 书籍
     */
    fun getBooks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getBooks(map)

        doRequest(books, object : Callback<BookStore>(view,screen) {
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
     * 获取分类
     */
    fun getBookType() {
        val type = RetrofitManager.service.getBookType()
        doRequest(type, object : Callback<BookStoreType>(view,screen) {
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