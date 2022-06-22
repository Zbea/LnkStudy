package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainMessageAdapter(layoutResId: Int, data: MutableList<MessageList>?) : BaseQuickAdapter<MessageList, BaseViewHolder>(layoutResId, data) {

    private var type=1

    fun setType(type:Int){
        this.type=type
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder, item: MessageList) {

        var tvName=helper.getView<TextView>(R.id.tv_message_name)
        var tvContent=helper.getView<TextView>(R.id.tv_message_content)

        tvName.text=item.name
        tvContent.text=item.content
        if(type==2){
            var tvTime=helper.getView<TextView>(R.id.tv_time)
            var checkBox=helper.getView<CheckBox>(R.id.cb_check)
            tvTime.text=item.createTime
            tvTime.visibility=if (type==1) View.GONE else View.VISIBLE
            checkBox.isChecked=item.isCheck
            checkBox.visibility=if (type==1) View.GONE else View.VISIBLE
            checkBox.setOnCheckedChangeListener { compoundButton, b ->
                item.isCheck=b
            }
        }
        else{
            if (!item.isLook){
                tvName.typeface=Typeface.defaultFromStyle(Typeface.BOLD)
                tvContent.typeface=Typeface.defaultFromStyle(Typeface.BOLD)
            }
            else{
                tvName.typeface=Typeface.defaultFromStyle(Typeface.NORMAL)
                tvContent.typeface=Typeface.defaultFromStyle(Typeface.NORMAL)
            }
        }
    }
}
