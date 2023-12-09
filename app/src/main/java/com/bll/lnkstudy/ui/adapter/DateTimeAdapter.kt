package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DateTimeAdapter(layoutResId: Int, data: List<Long>?) : BaseQuickAdapter<Long, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: Long) {
        helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item))
    }

}
