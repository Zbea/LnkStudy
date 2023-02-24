package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<BaseTypeBean>?) : BaseQuickAdapter<BaseTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BaseTypeBean) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            addOnClickListener(R.id.iv_edit)
            addOnClickListener(R.id.iv_delete)
            addOnClickListener(R.id.iv_top)
        }
    }

}
