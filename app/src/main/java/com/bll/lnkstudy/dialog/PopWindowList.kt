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
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PopWindowList(var context:Context, var list:MutableList<PopWindowBean>, var view: View,val xoff:Int,val yoff:Int) {

    private var mPopupWindow:PopupWindow?=null

    fun builder(): PopWindowList?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_list, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=true // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=true // 设置非PopupWindow区域可触摸


        var rvList=popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = MAdapter(R.layout.item_popwindow_list, list)
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
            mPopupWindow?.showAsDropDown(view,xoff, yoff,Gravity.RIGHT);
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    interface OnSelectListener{
        fun onSelect(item: PopWindowBean)
    }


    private class MAdapter(layoutResId: Int, data: List<PopWindowBean>?) : BaseQuickAdapter<PopWindowBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopWindowBean) {

            helper.setText(R.id.tv_name,item.name)
            helper.setVisible(R.id.iv_check,item.isCheck)

        }

    }

}