package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SToast


/**
 * book收藏、删除、移动
 */
class BookManageDialog(val context: Context,private val screenPos:Int, val type:Int, val book: BookBean){

    fun builder(): BookManageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_book_manage)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,430F))/2
        }
        dialog.show()

        val tv_name=dialog.findViewById<TextView>(R.id.tv_name)
        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        val ll_textbook=dialog.findViewById<LinearLayout>(R.id.ll_textbook)
        val ll_book=dialog.findViewById<LinearLayout>(R.id.ll_book)
        val ll_collect=dialog.findViewById<LinearLayout>(R.id.ll_collect)
        val ll_delete=dialog.findViewById<LinearLayout>(R.id.ll_delete)
        val ll_move=dialog.findViewById<LinearLayout>(R.id.ll_move)

        if (type==1){
            ll_collect.visibility= View.INVISIBLE
        }

        tv_name.text=book.bookName

        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        ll_collect.setOnClickListener {
            if (book.isCollect){
                SToast.showText(0,R.string.book_collected)
                return@setOnClickListener
            }
            if (onClickListener!=null)
                onClickListener?.onCollect()
            dialog.dismiss()
        }

        ll_delete.setOnClickListener {
            if (onClickListener!=null)
                onClickListener?.onDelete()
            dialog.dismiss()
        }

        ll_move.setOnClickListener {
            if (onClickListener!=null)
                onClickListener?.onMove()
            dialog.dismiss()
        }

        return this
    }



    private var onClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onCollect()
        fun onDelete()
        fun onMove()
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}