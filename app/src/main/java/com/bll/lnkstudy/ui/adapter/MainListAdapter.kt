package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainListAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemList) {
        helper.apply {
            setImageDrawable(R.id.iv_icon,if (item.isCheck) item.icon_check else item.icon)
            setText(R.id.tv_name,item.name)
        }
    }

    fun updateItem(position: Int,checked: Boolean){
        mData[position].isCheck = checked
        notifyItemChanged(position)
    }


}
