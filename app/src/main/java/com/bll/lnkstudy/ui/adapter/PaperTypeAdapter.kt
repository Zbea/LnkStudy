package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaperTypeAdapter(layoutResId: Int, data: List<PaperTypeBean>?) : BaseQuickAdapter<PaperTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaperTypeBean) {
        helper.apply {
            setVisible(R.id.ll_rank,item.isPg)
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_title,item.paperTitle)
            setText(R.id.tv_score,item.score.toString())
            if (item.isCloud){
                setText(R.id.tv_grade,"(${DataBeanManager.getGradeStr(item.grade)})" )
            }

        }
    }

}
