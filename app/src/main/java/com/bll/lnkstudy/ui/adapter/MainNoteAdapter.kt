package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.utils.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainNoteAdapter(layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, StringUtils.longToStringDataNoYear(item.date))
    }

}
