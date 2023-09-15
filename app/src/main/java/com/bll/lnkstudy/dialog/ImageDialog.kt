package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.GlideUtils
import java.io.File

class ImageDialog(val context: Context, val data:Any) {

    fun builder(): ImageDialog? {

         Dialog(context).apply {
            setContentView(R.layout.dialog_image)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()

            val iv_close=findViewById<ImageView>(R.id.iv_close)
            iv_close.setOnClickListener {
                dismiss()
            }

            val iv_image=findViewById<ImageView>(R.id.iv_image)
            if (data is String){
                GlideUtils.setImageRoundUrl(context,data,iv_image,10)
            }

            if (data is File){
                GlideUtils.setImageFileRound(context,data,iv_image,10)
            }
        }
        return this
    }


}