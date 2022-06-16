package com.bll.lnkstudy.ui.adapter

import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeWork
import com.bll.lnkstudy.mvp.model.TeachList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeWorkAdapter(layoutResId: Int, data: List<HomeWork>?) : BaseQuickAdapter<HomeWork, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeWork) {
        helper.setVisible(R.id.iv_pg,item.isPg)
        helper.setVisible(R.id.tv_message,item.isMessage)
        helper.setText(R.id.tv_message,item.message)

        var llContent=helper.getView<RelativeLayout>(R.id.ll_content)
        llContent.setBackgroundResource(item.resId)
    }



}
