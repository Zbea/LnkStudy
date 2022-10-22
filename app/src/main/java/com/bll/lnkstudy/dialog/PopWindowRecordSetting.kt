package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.bll.lnkstudy.R

class PopWindowRecordSetting(var context:Context, var view: View, val yoff:Int) {

    private var mPopupWindow:PopupWindow?=null
    private var width=0

    fun builder(): PopWindowRecordSetting?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_record_setting, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=true // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=true // 设置非PopupWindow区域可触摸

        val tv_edit=popView.findViewById<TextView>(R.id.tv_edit)
        tv_edit.setOnClickListener {
            dismiss()
            if (onClickListener!=null)
                onClickListener?.onClick(1)
        }
        val tv_delete=popView.findViewById<TextView>(R.id.tv_delete)
        tv_delete.setOnClickListener {
            dismiss()
            if (onClickListener!=null)
                onClickListener?.onClick(2)
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        width = mPopupWindow?.contentView?.measuredWidth!!

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
            mPopupWindow?.showAsDropDown(view,-width, yoff,Gravity.RIGHT);
        }
    }

   private var onClickListener:OnClickListener?=null

    fun setOnClickListener(onClickListener:OnClickListener)
    {
        this.onClickListener=onClickListener
    }

    fun interface OnClickListener{
        fun onClick(type: Int)
    }



}