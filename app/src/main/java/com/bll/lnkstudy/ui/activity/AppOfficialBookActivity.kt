package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.BookEvent
import com.bll.lnkstudy.mvp.model.BookStore
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_bookstore.*

class AppOfficialBookActivity: BaseAppCompatActivity(),
    IContractView.IBookStoreView {

    private var books= mutableListOf<Book>()
    private var mAdapter: BookStoreAdapter?=null
    private var pageCount = 0
    private var pageIndex = 1 //当前页码

    override fun onBookStore(bookStore: BookStore?) {
    }

    override fun onBuyBook(bookEvent: BookEvent?) {
    }

    override fun onDownBook(bookEvent: BookEvent?) {
    }


    override fun layoutId(): Int {
        return R.layout.ac_teach_list
    }

    override fun initData() {

    }

    override fun initView() {
        setPageTitle("官方书籍")

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->

        }

    }




}