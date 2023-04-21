package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaintingTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaintingTypeAdapter(layoutResId: Int, data: List<PaintingTypeBean>?) : BaseQuickAdapter<PaintingTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingTypeBean) {
        helper.apply {
            setImageResource(R.id.iv_painting,if (item.type==0) R.mipmap.icon_painting_sm else R.mipmap.icon_painting_yb)
            setText(R.id.tv_grade,DataBeanManager.grades[item.grade-1].desc)
        }
    }

}
