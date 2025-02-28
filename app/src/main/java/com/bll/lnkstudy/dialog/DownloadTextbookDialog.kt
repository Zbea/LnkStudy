package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.utils.GlideUtils


class DownloadTextbookDialog(private val context: Context, private val book: TextbookBean) {

    private var btn_ok:Button?=null
    private var dialog: Dialog?=null

    fun builder(): Dialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_book_detail)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        btn_ok = dialog?.findViewById(R.id.btn_ok)
        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_back)
        val iv_book = dialog?.findViewById<ImageView>(R.id.iv_book)
        val tv_price = dialog?.findViewById<TextView>(R.id.tv_price)
        val tv_course = dialog?.findViewById<TextView>(R.id.tv_course)
        val tv_version = dialog?.findViewById<TextView>(R.id.tv_version)
        val tv_info = dialog?.findViewById<TextView>(R.id.tv_info)
        val tv_book_name = dialog?.findViewById<TextView>(R.id.tv_book_name)

        GlideUtils.setImageRoundUrl(context,book.imageUrl,iv_book,5)

        tv_book_name?.text = book.bookName+"-"+ DataBeanManager.popupSemesters()[book.semester-1].name
        tv_price?.text = context.getString(R.string.price)+"： " + if (book.price==0) context.getString(R.string.free) else book.price
        tv_version?.text =context.getString(R.string.press)+"： " + DataBeanManager.getBookVersionStr(book.version)
        tv_info?.text = context.getString(R.string.introduction)+"： " + book.bookDesc
        tv_course?.text = context.getString(R.string.subject)+"： " + DataBeanManager.getCourseStr(book.subject)

        if (book.buyStatus == 1) {
            btn_ok?.setText(R.string.book_download_str)
        } else {
            btn_ok?.setText(R.string.book_buy_str)
        }

        if (book.loadSate==2)
            btn_ok?.visibility= View.GONE

        if (book.typeStr==DataBeanManager.textbookType[0])
            btn_ok?.visibility= View.GONE

        iv_cancel?.setOnClickListener { dialog?.dismiss() }
        btn_ok?.setOnClickListener { listener?.onClick() }

        return dialog
    }


    fun setChangeStatus() {
        book.buyStatus=1
        btn_ok?.isClickable = true
        btn_ok?.isEnabled = true
        btn_ok?.setText(R.string.book_download_str)
    }

    fun setUnClickBtn(string: String){
        if (btn_ok!=null){
            btn_ok?.text = string
            btn_ok?.isClickable = false
            btn_ok?.isEnabled = false//不能再按了
        }

    }

    fun setDissBtn(){
        btn_ok?.visibility = View.GONE
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