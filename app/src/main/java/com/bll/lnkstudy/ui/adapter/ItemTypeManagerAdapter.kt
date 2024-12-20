package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ItemTypeManagerAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            setGone(R.id.iv_edit,item.type!=7)
            setGone(R.id.iv_delete,item.type!=7)
            addOnClickListener(R.id.iv_edit,R.id.iv_top,R.id.iv_delete)
        }
    }

}
