package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PopWindowDrawingButton(val context:Context, val view: View,var list:MutableList<PopWindowBean>) {

    private var mPopupWindow:PopupWindow?=null
    private var height=0

    fun builder(): PopWindowDrawingButton?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_drawing_btn, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=true // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=true // 设置非PopupWindow区域可触摸

        var rvList=popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = MAdapter(R.layout.item_popwindow_btn, list)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if (onSelectListener!=null)
                onSelectListener?.onClick(list[position])
            dismiss()
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        height=mPopupWindow?.contentView?.measuredHeight!!
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
            mPopupWindow?.showAsDropDown(view,80, -(height+50))
        }
    }

    private class MAdapter(layoutResId: Int, data: List<PopWindowBean>?) : BaseQuickAdapter<PopWindowBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopWindowBean) {
            helper.setText(R.id.tv_name,item.name)
        }
    }

   private var onSelectListener:OnClickListener?=null

    fun setOnSelectListener(onSelectListener:OnClickListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnClickListener{
        fun onClick(item: PopWindowBean)
    }



}