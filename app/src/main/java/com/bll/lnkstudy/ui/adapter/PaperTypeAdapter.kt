package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaperTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaperTypeAdapter(layoutResId: Int, data: List<PaperTypeBean>?) : BaseQuickAdapter<PaperTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaperTypeBean) {
        helper.setVisible(R.id.ll_rank,item.isPg)
        helper.setText(R.id.tv_name,item.name)
    }



}
