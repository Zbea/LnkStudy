package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.PWDrawObjectHandler
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX


class PaintingLinerSelectDialog(private val context: Context){

    private var dialog:Dialog?=null
    private var ivLine1:ImageView?=null
    private var ivLine2:ImageView?=null
    private var ivLine3:ImageView?=null
    private var ivLine4:ImageView?=null

    private var tvPencil:TextView?=null
    private var tvPen:TextView?=null
    private var tvPenBall:TextView?=null
    private var tvPenBrush:TextView?=null

    private var currentType= PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
    private var currentWidth=3

    fun builder(): PaintingLinerSelectDialog {
        dialog= Dialog(context)
        dialog!!.setContentView(R.layout.dialog_painting_tool)
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog?.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,400F))/2
        dialog!!.show()

        val switch=dialog!!.findViewById<Switch>(R.id.sw_image)
        switch.setOnCheckedChangeListener { compoundButton, b ->
            onSelectListener?.setOpenRule(b)
        }

        tvPencil = dialog!!.findViewById(R.id.tv_pencil)
        tvPencil!!.setOnClickListener {
            setSelectPen(tvPencil!!,PWDrawObjectHandler.DRAW_OBJ_RANDOM_PENCIL)
        }

        tvPen= dialog!!.findViewById(R.id.tv_pen)
        tvPen!!.setOnClickListener {
            setSelectPen(tvPen!!,PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN)
        }

        tvPenBall= dialog!!.findViewById(R.id.tv_pen_ball)
        tvPenBall!!.setOnClickListener {
            setSelectPen(tvPenBall!!,PWDrawObjectHandler.DRAW_OBJ_RANDOM_BALLPEN)
        }

        tvPenBrush= dialog!!.findViewById(R.id.tv_pen_brush)
        tvPenBrush!!.setOnClickListener {
            setSelectPen(tvPenBrush!!,PWDrawObjectHandler.DRAW_OBJ_RANDOM_BRUSH)
        }

        ivLine1 = dialog!!.findViewById(R.id.line_1)
        ivLine1!!.setOnClickListener {
            setSelectImageView(ivLine1!!,1)
        }

        ivLine2= dialog!!.findViewById(R.id.line_2)
        ivLine2!!.setOnClickListener {
            setSelectImageView(ivLine2!!,3)
        }

        ivLine3= dialog!!.findViewById(R.id.line_3)
        ivLine3!!.setOnClickListener {
            setSelectImageView(ivLine3!!,5)
        }

        ivLine4 = dialog!!.findViewById(R.id.line_4)
        ivLine4!!.setOnClickListener {
            setSelectImageView(ivLine4!!,7)
        }

        setSelectPen(tvPen!!,currentType)
        setSelectImageView(ivLine2!!,currentWidth)


        return this
    }

    private fun setSelectPen(view:TextView,des: Int){
        tvPen?.setBackgroundResource(R.color.color_transparent)
        tvPencil?.setBackgroundResource(R.color.color_transparent)
        tvPenBall?.setBackgroundResource(R.color.color_transparent)
        tvPenBrush?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner)
        currentType=des
        onSelectListener?.setDrawType(currentType)
    }

    private fun setSelectImageView(view:ImageView,des: Int){
        ivLine1?.setBackgroundResource(R.color.color_transparent)
        ivLine2?.setBackgroundResource(R.color.color_transparent)
        ivLine3?.setBackgroundResource(R.color.color_transparent)
        ivLine4?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner)
        currentWidth=des
        onSelectListener?.setWidth(currentWidth)
    }

    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }

    private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    interface OnSelectListener{

        fun setWidth(width: Int)
        fun setDrawType(drawType:Int)
        fun setOpenRule(isOPen:Boolean)
    }

}