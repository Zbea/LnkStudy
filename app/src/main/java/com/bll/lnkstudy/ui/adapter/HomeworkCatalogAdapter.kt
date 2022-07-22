package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Homework
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkCatalogAdapter(layoutResId: Int, data: List<Homework>?) : BaseQuickAdapter<Homework, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Homework) {
        helper.setText(R.id.tv_num, item.title)
        helper.setText(R.id.tv_page, ""+item.page)
    }

}
