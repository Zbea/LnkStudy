package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils


class DrawingGeometryAxisDialog(val context: Context, private val screenPos:Int){

    private var isScale=false
    private var et_scale:EditText?=null
    private var dimension=10
    private var dialog:Dialog?=null

    fun builder(): DrawingGeometryAxisDialog {
        dialog = Dialog(context).apply {
            setContentView(R.layout.dialog_drawing_geometry_axis)
            val window = window!!
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = window.attributes
            if (screenPos==3){
                layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
                layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,400F))/2
            }
            show()

            val iv_close=findViewById<ImageView>(R.id.iv_close)
            iv_close.setOnClickListener {
                dismiss()
            }
            val cb_check=findViewById<CheckBox>(R.id.cb_check)
            val tv_description=findViewById<TextView>(R.id.tv_description)
            et_scale=findViewById(R.id.et_scale)
            val ll_axis1=findViewById<LinearLayout>(R.id.ll_axis1)
            val ll_axis2=findViewById<LinearLayout>(R.id.ll_axis2)
            val ll_axis3=findViewById<LinearLayout>(R.id.ll_axis3)

            cb_check.setOnCheckedChangeListener { compoundButton, b ->
                isScale=b
                tv_description.visibility=if (b) View.VISIBLE else View.GONE
                et_scale?.visibility=if (b) View.VISIBLE else View.GONE
            }

            ll_axis1.setOnClickListener {
                setScale(1)
            }
            ll_axis2.setOnClickListener {
                setScale(2)
            }
            ll_axis3.setOnClickListener {
                setScale(3)
            }

            setOnDismissListener {
                KeyboardUtils.hideSoftKeyboard(context)
            }

        }
        return this
    }


    private fun setScale(type: Int){
        val dimensionStr=et_scale?.text.toString()
        if (dimensionStr.isEmpty())return
        dimension=dimensionStr.toInt()
        if (isScale&&dimension>0)
        {
            onClickListener?.onClick(true,dimension,type)
        }else{
            onClickListener?.onClick(false,dimension,type)
        }
        dialog?.dismiss()
    }


    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(isScale:Boolean,value:Int,type:Int)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}