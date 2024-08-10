package com.bll.lnkstudy.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultiScoreAdapter(layoutResId: Int, var scoreType: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort, ToolUtils.numbers[item.sort+1])
        helper.setText(R.id.tv_score, item.score)

        val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext, 2)
        val mAdapter = ChildAdapter(R.layout.item_topic_child_score, scoreType, item.childScores)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(helper.adapterPosition, view, position)
        }
        mAdapter.setScoreMode(scoreType)
    }

    class ChildAdapter(layoutResId: Int, var scoreType: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort, "${item.sort+1}")
                helper.setText(R.id.tv_score, item.score)
                helper.setText(R.id.tv_score, if (scoreType == 1) item.score else if (item.result == 1) "对" else "错")
                helper.setImageResource(R.id.iv_result, if (item.result == 1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
                addOnClickListener(R.id.tv_score, R.id.iv_result)
            }
        }

        /**
         * 设置打分模式
         */
        fun setScoreMode(mode:Int){
            scoreType=mode
            notifyDataSetChanged()
        }
    }

    /**
     * 设置打分模式
     */
    fun setScoreMode(mode:Int){
        scoreType=mode
        notifyDataSetChanged()
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position: Int, view: View, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }
}
