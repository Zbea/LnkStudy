package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CourseAdapter(layoutResId: Int, data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tv_name,item)
    }

}
