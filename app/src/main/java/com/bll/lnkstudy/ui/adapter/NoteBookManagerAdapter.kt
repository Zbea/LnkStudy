package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            addOnClickListener(R.id.iv_edit)
            addOnClickListener(R.id.iv_delete)
            addOnClickListener(R.id.iv_top)
        }
    }

}
