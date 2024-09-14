package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.book.BookcaseDetailsBean
import com.bll.lnkstudy.ui.adapter.BookcaseDetailsAdapter
import com.bll.lnkstudy.widget.MaxRecyclerView
import com.bll.lnkstudy.widget.SpaceItemDeco

class BookcaseDetailsDialog(val context: Context) {

    fun builder(): BookcaseDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_bookcase_list)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val total=BookGreenDaoManager.getInstance().queryAllBook().size

        val tv_total=dialog.findViewById<TextView>(R.id.tv_book_total)
        tv_total.text="总计：${total}本"

        val items= mutableListOf<BookcaseDetailsBean>()
        for (typeStr in DataBeanManager.bookType){
            val item =BookcaseDetailsBean()
            item.typeStr=typeStr
            val books= BookGreenDaoManager.getInstance().queryAllBook(typeStr)
            if (books.size>0){
                item.num=books.size
                item.books=books
                items.add(item)
            }
        }

        val rv_list=dialog.findViewById<MaxRecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = BookcaseDetailsAdapter(R.layout.item_bookcase_list, items)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(20))

        return this
    }


}