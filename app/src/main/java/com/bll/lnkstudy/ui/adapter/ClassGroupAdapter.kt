package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: MutableList<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_groupNumber,if (item.state==1)ToolUtils.getFormatNum(item.classGroupId,"000000") else "")
        helper.setGone(R.id.tv_groupNumber,item.state==1)
        helper.setText(R.id.tv_teacher,item.teacher)
        helper.setText(R.id.tv_subject,item.subject)
        helper.setText(R.id.tv_num,"${item.studentCount}人")
        helper.addOnClickListener(R.id.tv_out,R.id.tv_info)
    }

}
