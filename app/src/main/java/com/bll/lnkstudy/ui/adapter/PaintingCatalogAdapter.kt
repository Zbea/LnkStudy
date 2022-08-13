package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaintingCatalogAdapter(layoutResId: Int, data: List<PaintingBean>?) : BaseQuickAdapter<PaintingBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingBean) {
        helper.setText(R.id.tv_num, item.title)
        helper.setText(R.id.tv_page, (item.page+1).toString())
    }

}
