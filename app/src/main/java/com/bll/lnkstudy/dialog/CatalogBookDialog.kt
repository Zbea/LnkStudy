package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.ui.adapter.BookCatalogAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.entity.MultiItemEntity


class CatalogBookDialog(val context: Context, private val oldScreen:Int, private val currentScreen:Int, val list: List<MultiItemEntity>, private val startCount:Int) {

    fun builder(): CatalogBookDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_drawing_catalog)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = if (oldScreen==1)Gravity.BOTTOM or  Gravity.END  else Gravity.BOTTOM or  Gravity.START
        layoutParams.x=if (currentScreen==3) DP2PX.dip2px(context,1021f+42f)else DP2PX.dip2px(context,42f)
        layoutParams.y= DP2PX.dip2px(context,5f)
        dialog.show()

        val rv_list = dialog.findViewById<RecyclerView>(R.id.rv_list)

        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = BookCatalogAdapter(list,startCount)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnCatalogClickListener(object : BookCatalogAdapter.OnCatalogClickListener {
            override fun onParentClick(page: Int) {
                dialog.dismiss()
                if (listener!=null)
                    listener?.onClick(page)
            }
            override fun onChildClick(page: Int) {
                dialog.dismiss()
                if (listener!=null)
                    listener?.onClick(page)
            }
        })
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(position: Int)
    }
    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }
}