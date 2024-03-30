package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDatePlanAdapter(layoutResId: Int, data: List<DatePlan>?) : BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlan) {
        helper.apply {
            setText(R.id.tv_start_time, item.startTimeStr)
            setText(R.id.tv_end_time, item.endTimeStr)
            setText(R.id.tv_course,item.course)
            setText(R.id.tv_content, item.content)
        }
    }
}
