package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupUserAdapter(layoutResId: Int, data: MutableList<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_job,item.job)
    }


}
