package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Paper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaperCatalogAdapter(layoutResId: Int, data: List<Paper>?) : BaseQuickAdapter<Paper, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Paper) {
        helper.setText(R.id.tv_num, item.title)
        helper.setText(R.id.tv_page, (item.page+1).toString())
    }

}
