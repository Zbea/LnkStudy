package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkType>?) : BaseQuickAdapter<HomeworkType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkType) {
        helper.setText(R.id.tv_name,item.name)
        helper.setImageResource(R.id.iv_image,ToolUtils.getImageResId(mContext,item.bgResId))
        helper.setVisible(R.id.ll_info,!item.isCreate)

        if (item.isPg){
            helper.setTextColor(R.id.tv_pg,mContext.getColor(R.color.black))
            helper.getView<TextView>(R.id.tv_pg).typeface= Typeface.defaultFromStyle(Typeface.BOLD)
        }
        else{
            helper.setTextColor(R.id.tv_pg,mContext.getColor(R.color.gray))
            helper.getView<TextView>(R.id.tv_pg).typeface= Typeface.defaultFromStyle(Typeface.NORMAL)
        }

        if (item.isMessage){
            helper.setTextColor(R.id.tv_message,mContext.getColor(R.color.black))
            helper.getView<TextView>(R.id.tv_message).typeface= Typeface.defaultFromStyle(Typeface.BOLD)
        }
        else{
            helper.setTextColor(R.id.tv_message,mContext.getColor(R.color.gray))
            helper.getView<TextView>(R.id.tv_message).typeface= Typeface.defaultFromStyle(Typeface.NORMAL)
        }

        if(item.state==2){
            helper.setText(R.id.tv_message,"收到题卷")
        }
        else{
            helper.setText(R.id.tv_message,"收到通知")
        }

        helper.addOnClickListener(R.id.ll_message)

    }



}
