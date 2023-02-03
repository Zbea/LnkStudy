package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDatePlanAdapter(layoutResId: Int, data: List<DatePlan>?) : BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlan) {
        helper.setText(R.id.tv_start_time, item.startTimeStr)
        helper.setText(R.id.tv_end_time, item.endTimeStr)
        helper.setText(R.id.tv_course, item.course)
        helper.setText(R.id.tv_title, item.content)
    }

}
