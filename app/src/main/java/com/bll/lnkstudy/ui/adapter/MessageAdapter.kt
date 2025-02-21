package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(layoutResId: Int, data: MutableList<MessageList.MessageBean>?) : BaseQuickAdapter<MessageList.MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageList.MessageBean) {
        var typeNameStr=""
        helper.apply {
            when(item.sendType){
                1->{
                    typeNameStr="来自："+item.teacherName
                }
                2->{
                    typeNameStr="发送："+item.teacherName
                }
                3-> {
                    typeNameStr="学校通知"
                }
                4->{
                    typeNameStr = (if (item.msgId==0) "发送：" else "来自：") +item.teacherName
                }
                5->{
                    typeNameStr="年级通知"
                }
            }
            setText(R.id.tv_message_name, typeNameStr)
            setText(R.id.tv_message_content,item.content)
            setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date))

        }
    }
}
