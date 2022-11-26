package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Notebook
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainNoteAdapter(layoutResId: Int, data: List<Notebook>?) : BaseQuickAdapter<Notebook, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Notebook) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.createDate))
    }

}
