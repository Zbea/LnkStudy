package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudDiaryAdapter(layoutResId: Int, data: List<DiaryBean>?) : BaseQuickAdapter<DiaryBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DiaryBean) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringWeek(item.date))
    }

}
