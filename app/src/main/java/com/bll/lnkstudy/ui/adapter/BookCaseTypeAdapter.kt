package com.bll.lnkstudy.ui.adapter

import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookCaseTypeAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemList) {
        val tv_name=helper.getView<TextView>(R.id.tv_name)
        tv_name.text=item.name
        if (item.isCheck)
        {
            tv_name.setBackgroundResource(R.drawable.bg_line_bottom_black)
            tv_name.setTextColor(mContext.resources.getColor(R.color.black))
        }
        else
        {
            tv_name.setBackgroundResource(R.color.color_transparent)
            tv_name.setTextColor(mContext.resources.getColor(R.color.black_90))
        }

    }

}
