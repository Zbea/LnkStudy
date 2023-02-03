package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DataList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 教学内容适配器
 */
class TeachListAdapter(layoutResId: Int, data: List<DataList>?) : BaseQuickAdapter<DataList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DataList) {

        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_info,item.info)
    }



}
