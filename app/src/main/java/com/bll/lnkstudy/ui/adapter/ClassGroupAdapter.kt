package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: MutableList<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_groupNumber,item.classNum)
        helper.setText(R.id.tv_teacher,item.teacher)
        helper.setText(R.id.tv_course,item.subject)
        helper.setText(R.id.tv_date,DateUtils.longToStringDataNoHour(item.date*1000))
        helper.addOnClickListener(R.id.tv_out)
    }


}
