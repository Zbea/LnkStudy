package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CalenderItemBean
import com.bll.lnkstudy.utils.GlideUtils


class DownloadCalenderDialog(private val context: Context, private val item: CalenderItemBean) {

    private var btn_ok:Button?=null
    private var dialog: Dialog?=null

    fun builder(): Dialog? {
        dialog= Dialog(context).apply {
            setContentView(R.layout.dialog_calender_detail)
            setCanceledOnTouchOutside(false)
            show()
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            btn_ok = findViewById(R.id.btn_ok)
            val iv_cancel = findViewById<ImageView>(R.id.iv_close)
            val iv_image = findViewById<ImageView>(R.id.iv_image)
            val tv_price =findViewById<TextView>(R.id.tv_price)
            val tv_info = findViewById<TextView>(R.id.tv_info)
            val tv_title = findViewById<TextView>(R.id.tv_title)
            val tv_year = findViewById<TextView>(R.id.tv_year)

            GlideUtils.setImageRoundUrl(context,item.imageUrl,iv_image,10)

            tv_title?.text = item.title
            tv_year?.text=context.getString(R.string.years)+"： "+item.year
            tv_price?.text =context.getString(R.string.price)+"： " + if (item.price==0) context.getString(R.string.free) else item.price
            tv_info?.text = "${context.getString(R.string.introduction)}： ${item.introduction}"


            if (item.buyStatus == 1) {
                btn_ok?.text = context.getString(R.string.book_download_str)
            } else {
                btn_ok?.text = context.getString(R.string.book_buy_str)
            }

            if (item.loadSate==2){
                btn_ok?.visibility= View.GONE
            }

            iv_cancel?.setOnClickListener { dismiss() }
            btn_ok?.setOnClickListener { listener?.onClick() }
        }
        return dialog
    }


    fun setChangeStatus() {
        item.buyStatus=1
        btn_ok?.text = context.getString(R.string.book_download_str)
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