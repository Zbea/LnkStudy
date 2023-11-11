package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(private val type:Int,layoutResId: Int, data: MutableList<MessageList.MessageBean>?) : BaseQuickAdapter<MessageList.MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageList.MessageBean) {
        var typeNameStr=""
        helper.apply {
            when(item.sendType){
                1->{
                    typeNameStr=mContext.getString(R.string.message_sender)+item.teacherName
                }
                2->{
                    typeNameStr=mContext.getString(R.string.message_receiver)+item.teacherName
                }
                3-> {
                    typeNameStr=mContext.getString(R.string.notice)
                }
            }
            setText(R.id.tv_message_name, typeNameStr)
            setText(R.id.tv_message_content,item.content)
            if (type==1){
                setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date*1000))
            }

        }
    }
}
