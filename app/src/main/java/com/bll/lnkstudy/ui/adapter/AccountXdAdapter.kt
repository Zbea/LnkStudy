package com.bll.lnkstudy.ui.adapter

import android.view.View
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AccountList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AccountXdAdapter(layoutResId: Int, data: List<AccountList.ListBean>?) : BaseQuickAdapter<AccountList.ListBean, BaseViewHolder>(layoutResId, data) {

    var mPosition = 0

    override fun convert(helper: BaseViewHolder, item: AccountList.ListBean) {
        helper.setText(R.id.tv_name,item.amount.toString())
        helper.setText(R.id.tv_price,item.price.toString()+"元")
        helper.setVisible(R.id.iv_select,helper.adapterPosition==mPosition)
    }

    fun setItemView(position: Int) {
        mPosition=position
        notifyDataSetChanged()
    }

}
