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

    private var isShow=true

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort, ToolUtils.numbers[item.sort+1])
        helper.setText(R.id.tv_score,if (scoreType==1) item.score.toString() else if (item.result==1)"对" else "错")
        helper.setGone(R.id.rv_list,!item.childScores.isNullOrEmpty())
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.setGone(R.id.iv_result,item.childScores.isNullOrEmpty())

        val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext, 2)
        val mAdapter = ChildAdapter(R.layout.item_topic_child_score, scoreType, item.childScores)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(helper.adapterPosition, view, position)
        }
        mAdapter.setScoreMode(scoreType)
        mAdapter.setResultView(isShow)

        helper.setGone(R.id.iv_result,isShow)

        helper.addOnClickListener(R.id.iv_result)
    }

    class ChildAdapter(layoutResId: Int, private var scoreType: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
        private var isShow=true

        override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort, "${item.sort+1}")
                helper.setText(R.id.tv_score, item.score.toString())
                helper.setText(R.id.tv_score, if (scoreType == 1) item.score.toString() else if (item.result == 1) "对" else "错")
                helper.setImageResource(R.id.iv_result, if (item.result == 1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
                helper.setGone(R.id.iv_result,isShow)
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

        fun setResultView(boolean: Boolean){
            isShow=boolean
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

    fun setResultView(boolean: Boolean){
        isShow=boolean
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
