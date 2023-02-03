package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DataList

class CampusModeVideoDialog(val context: Context, val dataList: DataList) {

    fun builder(): CampusModeVideoDialog? {

        val dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_campus_mode_video)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.show()

        val tvTitle = dialog?.findViewById<TextView>(R.id.tv_title)
        tvTitle.text=dataList.name

        val videoView=dialog?.findViewById<VideoView>(R.id.videoView)
        val mediacontroller = MediaController(context)
        videoView.setMediaController(mediacontroller)
        val uri = "android.resource://" + context.packageName + "/" + R.raw.video
        videoView.setVideoURI(Uri.parse(uri))
        videoView.start()

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            videoView.stopPlayback()
        }

        return this
    }

}