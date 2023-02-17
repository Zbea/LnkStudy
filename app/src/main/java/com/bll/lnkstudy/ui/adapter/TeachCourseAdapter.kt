package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

//教学科目适配器
class TeachCourseAdapter(layoutResId: Int, data: MutableList<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemList) {
        helper.setText(R.id.tv_name,item.desc)
    }

}
