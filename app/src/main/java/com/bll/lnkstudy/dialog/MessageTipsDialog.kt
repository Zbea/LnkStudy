package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList.MessageBean
import com.bll.lnkstudy.utils.DP2PX

class MessageTipsDialog(val context: Context, private val messageBean: MessageBean) {

    fun builder(): MessageTipsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_message_tips)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,1000f))/2
        layoutParams.y=DP2PX.dip2px(context,50f)
        dialog.show()

        var typeNameStr=""
        when(messageBean.sendType){
            1->{
                typeNameStr="来自：${messageBean.teacherName}"
            }
            3-> {
                typeNameStr="学校通知"
            }
            4->{
                typeNameStr="来自："+messageBean.teacherName
            }
            5->{
                typeNameStr="年级通知"
            }
        }

        val llMessage = dialog.findViewById<LinearLayout>(R.id.ll_message)
        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message_content)
        val tvName = dialog.findViewById<TextView>(R.id.tv_message_name)
        tvMessage.text=messageBean.content
        tvName.text=typeNameStr

        llMessage.setOnClickListener {
            dialog.dismiss()
        }

        return this
    }

}