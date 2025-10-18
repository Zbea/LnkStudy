package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.SystemUpdateInfo
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SPUtil


class AppUpdateDialog(private val context: Context,private val type:Int,private val item:Any){

    private var dialog:Dialog?=null
    private var btn_ok:TextView?=null
    private var tvCancel:TextView?=null
    private var tv_info:TextView?=null

    fun builder(): AppUpdateDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_update)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog!!.setCanceledOnTouchOutside(false)
        val window = dialog?.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 480f)) / 2
        dialog?.show()

        tvCancel = dialog?.findViewById(R.id.tv_cancel)
        tvCancel?.setOnClickListener {
            dismiss()
            if (type==1){
                SPUtil.putString(Constants.SP_UPDATE_APP_STATUS,"waiting")
            }
            else{
                SPUtil.putString(Constants.SP_UPDATE_SYSTEM_STATUS,"waiting")
            }
            listener?.onDelay()
        }

        btn_ok = dialog?.findViewById(R.id.tv_update)
        btn_ok?.setOnClickListener {
            if (type==1){
                tvCancel?.visibility= View.GONE
                listener?.onClick()
            }
            else{
                dismiss()
                AppUtils.startAPP(context,Constants.PACKAGE_SYSTEM_UPDATE)
            }
        }
        val tv_name = dialog?.findViewById<TextView>(R.id.tv_title)
        tv_info = dialog?.findViewById(R.id.tv_info)

        if(type==1){
            val item=item as AppUpdateBean
            tv_name?.text="应用更新："+item.versionName
            tv_info?.text=item.versionInfo
            btn_ok?.text="立即更新"
        }
        else{
            val item=item as SystemUpdateInfo
            tv_name?.text="系统更新："+item.version
            tv_info?.text=item.description
            btn_ok?.text="前往更新"
        }
        return this
    }

    fun show() {
        tvCancel?.visibility= View.VISIBLE
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun isShow():Boolean?{
        return dialog?.isShowing
    }

    fun setUpdateBtn(string: String){
        if (btn_ok!=null){
            btn_ok?.text = string
        }
    }

    var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick()
        fun onDelay()
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        listener = onDialogClickListener
    }
}