package com.bll.lnkstudy.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicTwoScoreAdapter(layoutResId: Int, private val scoreMode:Int,private val isResultShow:Boolean, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    constructor(layoutResId: Int,scoreMode: Int,data: List<ScoreItem>?):this(layoutResId,scoreMode,false,data)

    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,item.sortStr)
        helper.setText(R.id.tv_score,item.score.toString())
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.setGone(R.id.rv_list,!item.childScores.isNullOrEmpty())
        helper.setGone(R.id.iv_result,item.childScores.isNullOrEmpty()&&isResultShow)

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        val sharedPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(sharedPool)
        if (recyclerView.adapter==null){
            recyclerView?.layoutManager = GridLayoutManager(mContext,3)
            val mAdapter = ChildAdapter(R.layout.item_topic_score, scoreMode, isResultShow,item.childScores)
            recyclerView?.adapter = mAdapter
            mAdapter.setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,mAdapter,position)
            }
        }
        else{
            (recyclerView.adapter as ChildAdapter).setNewData(item.childScores)
        }

        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        val item=data[position]
        when {
            payloads.any { it == "updateScore" } -> {
                holder.setText(R.id.tv_score,item.score.toString())
                holder.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
            }
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(view: View,position:Int,childAdapter: ChildAdapter,childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }

    class ChildAdapter(layoutResId: Int, private val scoreMode:Int,private val isResultShow:Boolean, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ScoreItem) {
            helper.setText(R.id.tv_sort,item.sortStr)
            helper.setText(R.id.tv_score,if (scoreMode==1) item.score.toString() else if (item.result==1)"对" else "错")
            helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
            helper.setGone(R.id.iv_result,isResultShow)
            helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
            val item=data[position]
            when {
                payloads.any { it == "updateScore" } -> {
                    holder.setText(R.id.tv_score,if (scoreMode==1) item.score.toString() else if (item.result==1)"对" else "错")
                    holder.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
                }
                else -> super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

}
