package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NoteBook
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<NoteBook>?) : BaseQuickAdapter<NoteBook, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: NoteBook) {
        helper.setText(R.id.tv_name,item.name)
        helper.addOnClickListener(R.id.iv_edit)
        helper.addOnClickListener(R.id.iv_delete)
        helper.addOnClickListener(R.id.iv_top)
    }

}
