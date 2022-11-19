package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.utils.GlideUtils


class BookDetailsDialog(private val context: Context, private val book: Book) {

    private var btn_ok:Button?=null
    private var dialog: Dialog?=null

    fun builder(): Dialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_book_detail)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        btn_ok = dialog?.findViewById(R.id.btn_ok)
        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        val iv_book = dialog?.findViewById<ImageView>(R.id.iv_book)
        val tv_price = dialog?.findViewById<TextView>(R.id.tv_price)
        val tv_incetro = dialog?.findViewById<TextView>(R.id.tv_info)
        val tv_book_name = dialog?.findViewById<TextView>(R.id.tv_book_name)

        GlideUtils.setImageUrl(context,book.assetUrl,iv_book)

        tv_book_name?.text = book.name
        tv_price?.text = "价格： " + book.price
        tv_incetro?.text = "简介： " + book.description

        if (book.status == 1) {
            btn_ok?.text = "点击购买"
        } else {
            btn_ok?.text = "点击下载"
        }

        iv_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener { listener?.onClick() }

        return dialog
    }


    fun setChangeStatus(id:Int) {
        book.status=id
        if (book.status == 1) {
            btn_ok?.text = "点击购买"
        } else if (book.status == 2) {
            btn_ok?.text = "点击下载"
        }
    }

    fun setUnClickBtn(string: String){
        if (btn_ok!=null){
            btn_ok?.text = string
            btn_ok?.isClickable = false
            btn_ok?.isEnabled = false//不能再按了
        }

    }

    fun setChangeOk(){
        btn_ok?.text = "点击下载"
        btn_ok?.isClickable = true
        btn_ok?.isEnabled = true

    }

    fun dismiss(){
        dialog?.dismiss()
    }


    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onClick()
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}