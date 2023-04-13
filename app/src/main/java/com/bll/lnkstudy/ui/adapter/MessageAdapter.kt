package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(private val type:Int,layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        helper.apply {
            val typeNameStr=if (item.sendType==2) mContext.getString(R.string.message_receiver) else mContext.getString(R.string.message_sender)
            setText(R.id.tv_message_name, typeNameStr+item.teacherName)
            setText(R.id.tv_message_content,item.content)
            if (type==1){
                setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date*1000))
            }

        }
    }
}
