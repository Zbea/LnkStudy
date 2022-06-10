package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateRemind
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDateRemindAdapter(layoutResId: Int, data:List<DateRemind>?) : BaseQuickAdapter<DateRemind, BaseViewHolder>(layoutResId, data) {

    private var isShow=true

    override fun convert(helper: BaseViewHolder, item: DateRemind?) {
        if (data.size>0){
            helper.setText(R.id.tv_remind, item?.remind)
            helper.addOnClickListener(R.id.tv_clear)
            helper.setVisible(R.id.tv_clear,isShow)
        }
    }

    fun isShowClear(isShows:Boolean){
        isShow=isShows
        notifyDataSetChanged()
    }

}
