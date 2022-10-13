package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SToast


/**
 * book收藏、删除、移动
 */
class BookManageDialog(val context: Context,private val screenPos:Int, val type:Int, val book:Book){

    fun builder(): BookManageDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_book_manage, null)
        val dialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context,430F)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,430F))/2
        }

        window.attributes = layoutParams

        val tv_name=dialog.findViewById<TextView>(R.id.tv_name)
        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        val ll_content=dialog.findViewById<LinearLayout>(R.id.ll_content)
        val ll_content1=dialog.findViewById<LinearLayout>(R.id.ll_content1)
        val ll_collect=dialog.findViewById<LinearLayout>(R.id.ll_collect)
        val ll_delete=dialog.findViewById<LinearLayout>(R.id.ll_delete)
        val ll_move=dialog.findViewById<LinearLayout>(R.id.ll_move)

        ll_content.visibility= if (type==0) View.GONE else View.VISIBLE
        ll_content1.visibility= if (type==0) View.VISIBLE else View.GONE

        tv_name.text=book.name

        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        ll_collect.setOnClickListener {
            if (book.isCollect){
                SToast.showText(0,"已收藏")
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