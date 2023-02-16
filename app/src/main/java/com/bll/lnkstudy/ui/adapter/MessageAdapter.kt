package com.bll.lnkstudy.ui.adapter

import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageAdapter(layoutResId: Int, data: MutableList<MessageList>?) : BaseQuickAdapter<MessageList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageList) {
        item.run{
            helper.run {
                setText(R.id.tv_message_name,name)
                setText(R.id.tv_message_content,content)
                val tvTime=getView<TextView>(R.id.tv_message_time)
                if (tvTime!=null){
                    tvTime.text=createTime
                }
            }
        }
    }
}
