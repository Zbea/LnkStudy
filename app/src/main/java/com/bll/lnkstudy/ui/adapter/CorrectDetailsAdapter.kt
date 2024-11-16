package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.CorrectDetailsBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CorrectDetailsAdapter(layoutResId: Int, data: List<CorrectDetailsBean>?) : BaseQuickAdapter<CorrectDetailsBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectDetailsBean) {
        helper.setText(R.id.tv_type,item.typeStr)
        helper.setText(R.id.tv_title,item.title)
        helper.setGone(R.id.tv_answer,item.answerUrl.isNotEmpty())
        helper.setText(R.id.tv_score,"${item.score}" + if (item.scoreMode==1) "分" else "题" )
        helper.setGone(R.id.tv_score,item.score!=0.0)
        helper.setText(R.id.tv_date,"下发时间"+DateUtils.longToStringWeek(item.date))

        helper.addOnClickListener(R.id.tv_answer)
    }
}
