package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CourseBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainTextBookAdapter(layoutResId: Int, data: List<CourseBean>?) : BaseQuickAdapter<CourseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBean) {
        helper.setText(R.id.tv_course,item.name)
    }



}
