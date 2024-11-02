package com.bll.lnkstudy.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.book.BookcaseDetailsBean
import com.bll.lnkstudy.widget.FlowLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookcaseDetailsAdapter(layoutResId: Int, data: List<BookcaseDetailsBean>?) : BaseQuickAdapter<BookcaseDetailsBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookcaseDetailsBean) {
        helper.setText(R.id.tv_book_type,item.typeStr)
        helper.setText(R.id.tv_book_num,"小计："+item.num+"本")

        val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = FlowLayoutManager()
        val mAdapter = ChildAdapter(R.layout.item_bookcase_name,item.books)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(item.books[position])
        }
    }

    class ChildAdapter(layoutResId: Int,  data: List<BookBean>?) : BaseQuickAdapter<BookBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: BookBean) {
            helper.apply {
                helper.setText(R.id.tv_name, item.bookName)
            }
        }
    }


    private var listener: OnChildClickListener? = null

    fun interface OnChildClickListener {
        fun onClick(book: BookBean)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}
