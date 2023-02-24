package com.bll.lnkstudy.ui.adapter


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEventBean
import com.bll.lnkstudy.mvp.model.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanListAdapter(layoutResId: Int, data: List<DateEventBean>?) : BaseQuickAdapter<DateEventBean, BaseViewHolder>(layoutResId, data) {


    override fun convert(helper: BaseViewHolder, item: DateEventBean) {
        helper.apply {
            setText(R.id.tv_title, item.title)
            var weekStr=""
            for (week in item.weeks)
            {
                weekStr += "${week.name}  "
            }
            setText(R.id.tv_week,weekStr)
            setText(R.id.tv_date, "日期  "+item.startTimeStr+"~"+item.endTimeStr)
            getView<RecyclerView>(R.id.rv_list).apply {
                layoutManager = LinearLayoutManager(mContext)//创建布局管理
                val childAdapter= ChildAdapter(R.layout.item_date_plan_list_child, item.plans)
                adapter = childAdapter
                childAdapter?.bindToRecyclerView(this)
            }
            addOnClickListener(R.id.iv_delete)
        }
    }

    class ChildAdapter(layoutResId: Int, data: List<DatePlan>?) : BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DatePlan) {
            helper.apply {
                item.apply {
                    setText(R.id.tv_start_time, startTimeStr)
                    setText(R.id.tv_end_time, endTimeStr)
                    setText(R.id.tv_course,course)
                    setText(R.id.tv_content, content)
                    setVisible(R.id.tv_remind,isRemindStart||isRemindEnd)
                }
            }
        }
    }

}
