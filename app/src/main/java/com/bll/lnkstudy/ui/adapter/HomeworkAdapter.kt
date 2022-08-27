package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkType>?) : BaseQuickAdapter<HomeworkType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkType) {
        helper.setText(R.id.tv_name,item.name)
        helper.setImageResource(R.id.iv_image,ToolUtils.getImageResId(mContext,item.bgResId))
        if (item.isPg){
            helper.setTextColor(R.id.tv_pg,mContext.resources.getColor(R.color.black))
        }
        else{
            helper.setTextColor(R.id.tv_pg,mContext.resources.getColor(R.color.gray))
        }

        if (item.isMessage){
            helper.setTextColor(R.id.tv_message,mContext.resources.getColor(R.color.black))
        }
        else{
            helper.setTextColor(R.id.tv_message,mContext.resources.getColor(R.color.gray))
        }

        if(item.type==2||item.type==3){
            helper.setText(R.id.tv_message,"收到题卷")
        }
        else{
            helper.setText(R.id.tv_message,"收到通知")
        }

        helper.addOnClickListener(R.id.tv_message)
        helper.addOnClickListener(R.id.iv_message)


    }



}
