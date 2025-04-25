package com.bll.lnkstudy.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultistageScoreAdapter(layoutResId: Int, private val scoreMode:Int,  data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,item.sortStr)
        helper.setText(R.id.tv_score,item.score.toString())
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.setGone(R.id.rv_list,!item.childScores.isNullOrEmpty())
        helper.setGone(R.id.iv_result,item.childScores.isNullOrEmpty()&&item.isResultShow)

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        val sharedPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(sharedPool)
        if (recyclerView.adapter==null){
            recyclerView?.layoutManager = LinearLayoutManager(mContext)
            val mAdapter = TopicTwoScoreAdapter(R.layout.item_topic_multi_score,scoreMode, item.childScores)
            recyclerView?.adapter = mAdapter
            mAdapter.setCustomItemChildClickListener{ view, position,childAdapter, childPos ->
                listener?.onChildClick(view,helper.adapterPosition,mAdapter,position,childAdapter,childPos)
            }
            mAdapter.setOnItemChildClickListener { adapter, view, position ->
                listener?.onParentClick(view,helper.adapterPosition,mAdapter,position)
            }
        }
        else{
            (recyclerView.adapter as TopicTwoScoreAdapter).setNewData(item.childScores)
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

    interface OnItemChildClickListener {
        fun onParentClick(view: View,position:Int,twoAdapter: TopicTwoScoreAdapter, parentPosition:Int)
        fun onChildClick(view: View, position:Int, twoAdapter: TopicTwoScoreAdapter, parentPosition:Int, childAdapter: TopicTwoScoreAdapter.ChildAdapter, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }


}
