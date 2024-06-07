package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX


class PaintingLinerSelectDialog(private val context: Context){

    private var dialog:Dialog?=null
    private var ivLine1:ImageView?=null
    private var ivLine2:ImageView?=null
    private var ivLine3:ImageView?=null
    private var ivLine4:ImageView?=null

    fun builder(): PaintingLinerSelectDialog {
        dialog= Dialog(context)
        dialog!!.setContentView(R.layout.dialog_painting_tool)
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog?.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,400F))/2
        dialog!!.show()

        ivLine1 = dialog!!.findViewById(R.id.line_1)
        ivLine1!!.setOnClickListener {
            setSelectImageView(ivLine1!!,1)
        }

        ivLine2= dialog!!.findViewById(R.id.line_2)
        ivLine2!!.setOnClickListener {
            setSelectImageView(ivLine2!!,4)
        }

        ivLine3= dialog!!.findViewById(R.id.line_3)
        ivLine3!!.setOnClickListener {
            setSelectImageView(ivLine3!!,6)
        }

        ivLine4 = dialog!!.findViewById(R.id.line_4)
        ivLine4!!.setOnClickListener {
            setSelectImageView(ivLine4!!,8)
        }


        return this
    }

    private fun setSelectImageView(view:ImageView,des: Int){
        ivLine1?.setBackgroundResource(R.color.color_transparent)
        ivLine2?.setBackgroundResource(R.color.color_transparent)
        ivLine3?.setBackgroundResource(R.color.color_transparent)
        ivLine4?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner)
        onSelectListener?.onSelect(des)
        dismiss()
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

    fun interface OnSelectListener{
        fun onSelect(des: Int)
    }

}