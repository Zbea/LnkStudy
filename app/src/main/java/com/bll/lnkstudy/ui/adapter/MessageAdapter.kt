package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(private val type:Int,layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        helper.apply {
            setText(R.id.tv_message_name,item.teacherName)
            setText(R.id.tv_message_content,item.content)
            if (type==1){
                setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date))
            }

        }
    }
}
