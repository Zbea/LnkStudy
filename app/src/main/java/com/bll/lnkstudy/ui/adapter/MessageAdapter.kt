package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(private val type:Int,layoutResId: Int, data: MutableList<MessageList.MessageBean>?) : BaseQuickAdapter<MessageList.MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageList.MessageBean) {
        helper.apply {
            val typeNameStr=when(item.sendType){
                2->{
                    mContext.getString(R.string.message_sender)+item.teacherName
                }
                3->{
                    mContext.getString(R.string.notice)
                }
                else -> {
                    mContext.getString(R.string.message_receiver)+item.teacherName
                }
            }
            setText(R.id.tv_message_name, typeNameStr)
            setText(R.id.tv_message_content,"内容："+item.content)
            if (type==1){
                setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date*1000))
            }

        }
    }
}
