package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean

class CampusModeVideoDialog(val context: Context, val listBean: ListBean) {

    fun builder(): CampusModeVideoDialog? {

        val dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_campus_mode_video)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.show()

        val tvTitle = dialog?.findViewById<TextView>(R.id.tv_title)
        tvTitle.text=listBean.name

        val jzVd=dialog?.findViewById<JzvdStd>(R.id.jz_vd)
        jzVd.setUp(listBean?.address,"",Jzvd.SCREEN_NORMAL)
        jzVd.startPreloading() //开始预加载，加载完等待播放
        jzVd.startVideoAfterPreloading() //如果预加载完会开始播放，如果未加载则开始加载


        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            Jzvd.releaseAllVideos()
        }

        return this
    }

}