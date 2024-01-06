package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainCorrectAdapter(layoutResId: Int, data: List<HomeworkDetailsBean>?) : BaseQuickAdapter<HomeworkDetailsBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkDetailsBean) {
        helper.apply {
            setText(R.id.tv_name,"（${item.HomeworkTypeStr}）")
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.time))
            setText(R.id.tv_course, item.course)
            setText(R.id.tv_content,item.content)
        }
    }

}
