package com.bll.lnkstudy.ui.adapter

import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, private var scoreType:Int, private var module:Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    private var isShow=true

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort+1] else "${item.sort+1}")
        helper.getView<TextView>(R.id.tv_sort).layoutParams.width=if (module==1) DP2PX.dip2px(mContext,55f) else DP2PX.dip2px(mContext,40f)
        helper.setText(R.id.tv_score,if (scoreType==1)item.score.toString() else if (item.result==1)"对" else "错" )
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)

        helper.setGone(R.id.iv_result,isShow)

        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }

    /**
     * 设置模板
     */
    fun setCorrectMode(mode:Int){
        module=mode
        notifyDataSetChanged()
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
