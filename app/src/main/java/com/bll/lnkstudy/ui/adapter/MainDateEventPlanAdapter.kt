package com.bll.lnkstudy.ui.adapter


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.bll.lnkstudy.mvp.model.DatePlanEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDateEventPlanAdapter(layoutResId: Int, data: List<DatePlanEvent>?) : BaseQuickAdapter<DatePlanEvent, BaseViewHolder>(layoutResId, data) {


    override fun convert(helper: BaseViewHolder, item: DatePlanEvent) {
        helper.setText(R.id.tv_time_start, item.startTimeStr)
        helper.setText(R.id.tv_time_end, item.endTimeStr)

        var rv=helper.getView<RecyclerView>(R.id.rv_plan_child)
        rv.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        val childAdapter= ChildAdapter(R.layout.item_date_plan_event_child, item.list)
        rv.adapter = childAdapter
        childAdapter?.bindToRecyclerView(rv)
    }

    class ChildAdapter(layoutResId: Int, data: List<DatePlanBean>?) : BaseQuickAdapter<DatePlanBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DatePlanBean) {
            helper.setText(R.id.tv_start_time, item.startTimeStr)
            helper.setText(R.id.tv_end_time, item.endTimeStr)
            helper.setText(R.id.tv_content, item.content)
        }



    }

}
