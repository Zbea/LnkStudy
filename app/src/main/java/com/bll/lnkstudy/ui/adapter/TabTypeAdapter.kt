package com.bll.lnkstudy.ui.adapter

import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TabTypeAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            val tv_name=getView<TextView>(R.id.tv_name)
            tv_name.isSelected=item.isCheck
            tv_name.setTextColor(mContext.resources.getColor(if (item.isCheck)R.color.black else R.color.black_90))
        }
    }

}
