package com.bll.lnkstudy.ui.fragment.resource

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.book.TextbookStore
import com.bll.lnkstudy.mvp.presenter.DownloadDictionaryPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.rv_list

class DictionaryDownloadFragment:BaseMainFragment(),IContractView.IDictionaryResourceView {

    private var presenter= DownloadDictionaryPresenter(this,getScreenPosition())
    private var books = mutableListOf<TextbookBean>()
    private var mAdapter: TextBookAdapter? = null

    override fun onList(bookStore: TextbookStore?) {

    }
    override fun buySuccess() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initChangeScreenData()
        pageSize=12

        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            fetchData()
        }
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(requireActivity(), 4)//创建布局管理
        mAdapter = TextBookAdapter(R.layout.item_bookstore, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
//            this.position = position
//            showBookDetails(books[position])
        }
    }

    override fun initChangeScreenData() {
        super.initChangeScreenData()
        presenter= DownloadDictionaryPresenter(this,getScreenPosition())
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        presenter.getList(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}