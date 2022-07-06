package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BookStoreType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PopWindowBookStoreType(var context:Context,var list:MutableList<BookStoreType>,var view: View) {

    private var mPopupWindow:PopupWindow?=null

    fun builder(): PopWindowBookStoreType?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_bookstore_type, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=true // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=true // 设置非PopupWindow区域可触摸
        if (list.size>4){
            mPopupWindow?.height=600
        }

        var rvList=popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = MAdapter(R.layout.item_bookstore_type, list)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            for (item in list)
            {
                item.isCheck=false
            }
            list[position].isCheck=true
            mAdapter?.notifyDataSetChanged()
            if (onSelectListener!=null)
                onSelectListener?.onSelect(list[position])
            dismiss()
        }
        show()
        return this
    }

    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view,-297, 5,Gravity.RIGHT);
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    interface OnSelectListener{
        fun onSelect(bookStoreType: BookStoreType)
    }


    private class MAdapter(layoutResId: Int, data: List<BookStoreType>?) : BaseQuickAdapter<BookStoreType, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: BookStoreType) {

            helper.setText(R.id.tv_name,item.title)
            helper.setVisible(R.id.iv_check,item.isCheck)

        }

    }

}