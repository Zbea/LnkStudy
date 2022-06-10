package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDateAdapter(layoutResId: Int, data: List<DatePlanBean>?) : BaseQuickAdapter<DatePlanBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlanBean) {
        helper.setText(R.id.tv_start_time, item.startTimeStr)
        helper.setText(R.id.tv_end_time, item.endTimeStr)
        helper.setText(R.id.tv_content, item.content)
    }



}
