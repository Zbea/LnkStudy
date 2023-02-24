package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainClassGroupAdapter(layoutResId: Int, data: MutableList<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_groupNumber,item.classNum)
            setText(R.id.tv_teacher,item.teacher)
            setText(R.id.tv_course,item.subject)
        }
    }


}
