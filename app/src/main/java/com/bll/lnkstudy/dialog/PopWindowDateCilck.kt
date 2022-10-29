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

class PopWindowDateCilck(var context:Context, var view: View) {

    private var mPopupWindow:PopupWindow?=null
    private var width=0

    fun builder(): PopWindowDateCilck?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popwindow_date_switch_view, null, false)
        mPopupWindow = PopupWindow(context)
        mPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置PopupWindow的内容view
        mPopupWindow?.contentView=popView
        mPopupWindow?.isFocusable=true // 设置PopupWindow可获得焦点
        mPopupWindow?.isTouchable=true // 设置PopupWindow可触摸
        mPopupWindow?.isOutsideTouchable=true // 设置非PopupWindow区域可触摸

        val tvPlan = popView.findViewById<TextView>(R.id.tv_plan)
        tvPlan.setOnClickListener {
            dismiss()
            onClickListener?.onClick(0)
        }
        val tvSchedule = popView.findViewById<TextView>(R.id.tv_schedule)
        tvSchedule.setOnClickListener {
            dismiss()
            onClickListener?.onClick(1)
        }
        val tvDay = popView.findViewById<TextView>(R.id.tv_day)
        tvDay.setOnClickListener {
            dismiss()
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
            mPopupWindow?.showAsDropDown(view,-width, 5,Gravity.RIGHT);
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