package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AccountXDList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AccountXdAdapter(layoutResId: Int, data: List<AccountXDList.ListBean>?) : BaseQuickAdapter<AccountXDList.ListBean, BaseViewHolder>(layoutResId, data) {

    var mPosition = 0

    override fun convert(helper: BaseViewHolder, item: AccountXDList.ListBean) {
        helper.setText(R.id.tv_name,item.amount.toString())
        if (helper.adapterPosition==mPosition){
            helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_gray_solid_5dp_corner)
            helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.white) )
        }
        else{
            helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_gray_stroke_5dp_corner)
            helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
        }
    }

    fun setItemView(position: Int) {
        mPosition=position
        notifyDataSetChanged()
    }

}
