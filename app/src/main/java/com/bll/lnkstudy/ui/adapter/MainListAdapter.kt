package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MainListBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainListAdapter(layoutResId: Int, data: List<MainListBean>?) : BaseQuickAdapter<MainListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MainListBean) {
        var ivImage=helper.getView<ImageView>(R.id.iv_icon)
        var tvName=helper.getView<TextView>(R.id.tv_name)
        tvName.text=item.name
        if (item.checked){
            ivImage.setImageDrawable(item.icon_check)
            tvName.setTextColor(mContext.getColor(R.color.black))
        }
        else{
            ivImage.setImageDrawable(item.icon)
            tvName.setTextColor(mContext.getColor(R.color.black))
        }
    }

    fun updateItem(position: Int,checked: Boolean){
        val et = mData[position]
        et.checked = checked
        mData[position] = et
        notifyItemChanged(position)
    }


}
