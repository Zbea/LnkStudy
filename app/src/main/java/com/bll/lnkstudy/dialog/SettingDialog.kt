package com.bll.lnkstudy.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SystemSettingUtils


/**
 * 控制中心
 */
class SettingDialog(val context: Context){

    var dialog:AlertDialog?=null

    fun builder(): SettingDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_setting, null)
        dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context,600f)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val llBook=dialog?.findViewById<LinearLayout>(R.id.ll_bookstore)
        llBook?.setOnClickListener {
            dialog?.dismiss()
            if (onClickListener!=null)
                onClickListener?.onClickBookStore()

        }
        val llApp=dialog?.findViewById<LinearLayout>(R.id.ll_appstore)
        llApp?.setOnClickListener {
            dialog?.dismiss()
            if (onClickListener!=null)
                onClickListener?.onClickAppStore()
        }


        val llSet=dialog?.findViewById<LinearLayout>(R.id.ll_setting)
        llSet?.setOnClickListener {
            SystemSettingUtils.gotoSystemSetting(context)
        }

        val llFx=dialog?.findViewById<LinearLayout>(R.id.ll_fx)
        llFx?.setOnClickListener {
            onClickListener?.onClickAirPlaneMode()
        }

        val llScreen=dialog?.findViewById<LinearLayout>(R.id.ll_screen)
        llScreen?.setOnClickListener {
            dialog?.dismiss()
            SystemSettingUtils.saveScreenShot(context as Activity)
        }

        val llRecycle=dialog?.findViewById<LinearLayout>(R.id.ll_recycler)
        llRecycle?.setOnClickListener {
            dialog?.dismiss()
            onClickListener?.onRecycleBin()
        }

        var volume=SystemSettingUtils.getMediaVolume(context)
        var max=SystemSettingUtils.getMediaMaxVolume(context)
        val seekBar=dialog?.findViewById<SeekBar>(R.id.seekBar)
        seekBar?.max=max
        seekBar?.progress=volume
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                volume=p1
                SystemSettingUtils.setMediaVolume(context, volume)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        val ivSub=dialog?.findViewById<ImageView>(R.id.iv_sub)
        val ivAdd=dialog?.findViewById<ImageView>(R.id.iv_add)
        ivSub?.setOnClickListener {
            if (volume!!>0){
                volume -=1
                seekBar?.progress=volume
            }
        }
        ivAdd?.setOnClickListener {
            if (volume!!<max){
                volume+=1
                seekBar?.progress=volume
            }
        }

        return this
    }

    fun show(){
        if (dialog!=null)
            dialog?.show()
    }


    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClickBookStore()
        fun onClickAppStore()
        fun onClickAirPlaneMode()
        fun onRecycleBin()
    }

    fun setOnDialogClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
    }



}