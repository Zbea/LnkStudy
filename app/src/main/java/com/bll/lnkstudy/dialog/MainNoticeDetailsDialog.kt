package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils


class MainNoticeDetailsDialog(private val context: Context, private val item: HomeworkNoticeList.HomeworkNoticeBean) {

    private var dialog: Dialog?=null

    fun builder(): Dialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_main_notice_details)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog?.show()

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_close)
        ivCancel?.setOnClickListener { dialog?.dismiss() }
        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        val tvTime = dialog?.findViewById<TextView>(R.id.tv_time)
        val tvContent = dialog?.findViewById<TextView>(R.id.tv_content)
        val tv_end_time = dialog?.findViewById<TextView>(R.id.tv_end_time)

        tvName?.text=item.name
        tvTime?.text= "发送时间："+DateUtils.longToStringDataNoYear(item.time)
        if (item.endTime>0){
            tv_end_time?.visibility= View.VISIBLE
            tv_end_time?.text= "提交时间："+DateUtils.longToStringWeek(item.endTime)
        }
        tvContent?.text=item.title

        return dialog
    }




}