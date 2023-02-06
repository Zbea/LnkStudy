package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.GlideUtils
import java.io.File

class ImageDialog(val context: Context, val data:Any) {

    fun builder(): ImageDialog? {

        val dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_image)
        dialog?.show()
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)

        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        val iv_image=dialog.findViewById<ImageView>(R.id.iv_image)
        if (data is String){
            GlideUtils.setImageRoundUrl(context,data,iv_image,1)
        }

        if (data is File){
            GlideUtils.setImageFile(context,data,iv_image)
        }


        return this
    }


}