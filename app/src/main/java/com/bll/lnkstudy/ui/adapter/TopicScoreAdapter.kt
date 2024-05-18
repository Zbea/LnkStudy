package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
    private var module=0
    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort] else item.sort.toString())
        helper.setText(R.id.tv_score,item.score)
    }

    fun setChangeModule(type:Int){
        module=type
        notifyDataSetChanged()
    }

}
