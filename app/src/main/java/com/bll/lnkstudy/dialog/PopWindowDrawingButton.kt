package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.bll.lnkstudy.R

class PopWindowDrawingButton(val context:Context, val view: View, val type: Int,val yoff:Int) {

    private var mPopupWindow:PopupWindow?=null

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

        val ll_content=popView.findViewById<LinearLayout>(R.id.ll_content)

        val tv_save=popView.findViewById<TextView>(R.id.tv_save)
        tv_save.setOnClickListener {
            dismiss()
            if (onSelectListener!=null)
                onSelectListener?.onClick(1)
        }

        val tv_commit=popView.findViewById<TextView>(R.id.tv_commit)
        tv_commit.setOnClickListener {
            dismiss()
            if (onSelectListener!=null)
                onSelectListener?.onClick(2)
        }

        val tv_delete=popView.findViewById<TextView>(R.id.tv_delete)
        tv_delete.setOnClickListener {
            dismiss()
            if (onSelectListener!=null)
                onSelectListener?.onClick(3)
        }

        val tv_assist=popView.findViewById<TextView>(R.id.tv_assist)
        tv_assist.setOnClickListener {
            dismiss()
            if (onSelectListener!=null)
                onSelectListener?.onClick(4)
        }

        if (type==0){
            tv_save.visibility=View.VISIBLE
            tv_commit.visibility=View.VISIBLE
            tv_delete.visibility=View.VISIBLE
            tv_assist.visibility=View.GONE
        }
        else if (type==1){
            tv_save.visibility=View.GONE
            tv_commit.visibility=View.VISIBLE
            tv_delete.visibility=View.GONE
            tv_assist.visibility=View.GONE
        }
        else if (type==2){
            tv_save.visibility=View.GONE
            tv_commit.visibility=View.GONE
            tv_delete.visibility=View.VISIBLE
            tv_assist.visibility=View.VISIBLE
        }
        else if (type==3){
            tv_save.visibility=View.GONE
            tv_commit.visibility=View.GONE
            tv_delete.visibility=View.VISIBLE
            tv_assist.visibility=View.GONE
        }
        else if (type==4){
            tv_save.visibility=View.VISIBLE
            tv_commit.visibility=View.GONE
            tv_delete.visibility=View.VISIBLE
            tv_assist.visibility=View.GONE
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
            mPopupWindow?.showAsDropDown(view,-20, yoff)
        }
    }

    fun isShow(): Boolean? {
        return if (mPopupWindow != null) {
            mPopupWindow?.isShowing
        } else{
            false
        }
    }

   private var onSelectListener:OnClickListener?=null

    fun setOnSelectListener(onSelectListener:OnClickListener)
    {
        this.onSelectListener=onSelectListener
    }

    interface OnClickListener{
        fun onClick(type: Int)
    }



}