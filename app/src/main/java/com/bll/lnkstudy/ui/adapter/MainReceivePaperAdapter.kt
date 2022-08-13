package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ReceivePaper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainReceivePaperAdapter(layoutResId: Int, data: List<ReceivePaper>?) : BaseQuickAdapter<ReceivePaper, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ReceivePaper) {
        helper.setText(R.id.tv_type,if (item.type==0) "${item.course}作业" else "${item.course}考卷")
        helper.setText(R.id.tv_count,"${item.images.size}")
        helper.setText(R.id.tv_title,item.title)

    }



}
