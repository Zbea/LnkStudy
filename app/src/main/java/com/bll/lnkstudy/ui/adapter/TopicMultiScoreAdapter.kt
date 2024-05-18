package com.bll.lnkstudy.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultiScoreAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort, ToolUtils.numbers[item.sort])
        helper.setText(R.id.tv_score,item.score)

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext,6)
        val mAdapter = ChildAdapter(R.layout.item_topic_child_score, item.childScores)
        recyclerView?.adapter = mAdapter
    }


    class ChildAdapter(layoutResId: Int,  data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort,item.sort.toString())
                helper.setText(R.id.tv_score,item.score)
            }
        }
    }

}
