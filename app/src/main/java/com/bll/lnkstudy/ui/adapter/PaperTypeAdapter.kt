package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.utils.SPUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.*

class PaperTypeAdapter(layoutResId: Int, data: List<PaperTypeBean>?) : BaseQuickAdapter<PaperTypeBean, BaseViewHolder>(layoutResId, data) {

    var grade = Objects.requireNonNull(SPUtil.getObj("user", User::class.java))?.grade

    override fun convert(helper: BaseViewHolder, item: PaperTypeBean) {
        helper.apply {
            setVisible(R.id.ll_rank,item.isPg)
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_title,item.paperTitle)
            setText(R.id.tv_score,"分数："+item.score.toString())
            if (grade!=item.grade){
                setText(R.id.tv_grade,"(${DataBeanManager.getGradeStr(item.grade)})" )
            }

        }
    }

}
