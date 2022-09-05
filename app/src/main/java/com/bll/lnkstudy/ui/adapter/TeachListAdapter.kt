package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 教学内容适配器
 */
class TeachListAdapter(layoutResId: Int, data: List<ListBean>?) : BaseQuickAdapter<ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ListBean) {

        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_info,item.info)
    }



}
