package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDatePlanAdapter(layoutResId: Int, data: List<DatePlan>?) : BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlan) {
        helper.apply {
            item.apply {
                setText(R.id.tv_start_time, startTimeStr)
                setText(R.id.tv_end_time, endTimeStr)
                setText(R.id.tv_course, course)
                setText(R.id.tv_title, content)
            }
        }
    }
}
