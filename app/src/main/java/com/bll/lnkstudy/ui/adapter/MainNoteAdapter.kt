package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainNoteAdapter(layoutResId: Int, data: List<NotebookBean>?) : BaseQuickAdapter<NotebookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: NotebookBean) {
        helper.apply {
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.createDate))
        }
    }

}
