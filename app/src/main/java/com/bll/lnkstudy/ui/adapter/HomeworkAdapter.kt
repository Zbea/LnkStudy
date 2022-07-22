package com.bll.lnkstudy.ui.adapter

import android.widget.RelativeLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkType>?) : BaseQuickAdapter<HomeworkType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkType) {
        helper.setText(R.id.tv_name,item.name)
        helper.setVisible(R.id.iv_pg,item.isPg)

        var llContent=helper.getView<RelativeLayout>(R.id.ll_content)
        llContent.setBackgroundResource(item.resId)
    }



}
