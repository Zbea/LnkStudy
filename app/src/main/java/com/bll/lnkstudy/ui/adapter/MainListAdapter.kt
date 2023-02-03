package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MainList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainListAdapter(layoutResId: Int, data: List<MainList>?) : BaseQuickAdapter<MainList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MainList) {
        var ivImage=helper.getView<ImageView>(R.id.iv_icon)
        helper.setText(R.id.tv_name,item.name)
        ivImage.setImageDrawable(if (item.checked) item.icon_check else item.icon)

    }

    fun updateItem(position: Int,checked: Boolean){
        mData[position].checked = checked
        notifyItemChanged(position)
    }


}
