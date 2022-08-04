package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.bll.lnkstudy.R

class PopWindowDrawSetting(var context:Context, var view: View?) {

    private var mPopupWindow:PopupWindow?=null

    fun builder(): PopWindowDrawSetting?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_draw_setting, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=false // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=false // 设置非PopupWindow区域可触摸


        val tv_clear=popView.findViewById<TextView>(R.id.tv_clear)
        tv_clear.setOnClickListener {
            if (onSelectListener!=null)
                onSelectListener?.onSelect(1)
        }

        val tv_clear_all=popView.findViewById<TextView>(R.id.tv_clear_all)
        tv_clear_all.setOnClickListener {
            if (onSelectListener!=null)
                onSelectListener?.onSelect(2)
        }

        val tv_fine=popView.findViewById<TextView>(R.id.tv_fine)
        tv_fine.setOnClickListener {
            if (onSelectListener!=null)
                onSelectListener?.onSelect(3)
        }

        val tv_thick=popView.findViewById<TextView>(R.id.tv_thick)
        tv_thick.setOnClickListener {
            if (onSelectListener!=null)
                onSelectListener?.onSelect(4)
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
            mPopupWindow?.showAsDropDown(view,-2, -410)
        }
    }

    fun isShow(): Boolean? {
        return if (mPopupWindow != null) {
            mPopupWindow?.isShowing
        } else{
            false
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    interface OnSelectListener{
        fun onSelect(type: Int)
    }



}