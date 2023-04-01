package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ReceivePaper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainReceivePaperAdapter(layoutResId: Int, data: List<ReceivePaper.PaperBean>?) : BaseQuickAdapter<ReceivePaper.PaperBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ReceivePaper.PaperBean) {
        helper.setText(R.id.tv_course,item.subject)
        helper.setText(R.id.tv_type,item.examName)
    }

}
