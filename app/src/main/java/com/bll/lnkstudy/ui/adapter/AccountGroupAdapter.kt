package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AccountGroupAdapter(layoutResId: Int, data: MutableList<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name.toString())
        helper.setText(R.id.tv_group,(helper.adapterPosition+1).toString())
        helper.setText(R.id.tv_groupNumber,item.groupNumber)
        helper.setText(R.id.tv_teacher,item.teacher)
        helper.setText(R.id.tv_course,item.course)
        helper.setText(R.id.tv_number,item.number.toString())
        helper.addOnClickListener(R.id.tv_out)
    }


}
