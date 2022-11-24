package com.bll.lnkstudy.ui.adapter


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanListAdapter(layoutResId: Int, data: List<DateEvent>?) : BaseQuickAdapter<DateEvent, BaseViewHolder>(layoutResId, data) {


    override fun convert(helper: BaseViewHolder, item: DateEvent) {
        helper.setText(R.id.tv_title, item.title)
        var weekStr=""
        for (week in item.weeks)
        {
            weekStr += "${week.name}  "
        }
        helper.setText(R.id.tv_week,weekStr)
        helper.setText(R.id.tv_date, "日期  "+item.startTimeStr+"~"+item.endTimeStr)

        var rv=helper.getView<RecyclerView>(R.id.rv_list)
        rv.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        val childAdapter= ChildAdapter(R.layout.item_date_plan_list_child, item.plans)
        rv.adapter = childAdapter
        childAdapter?.bindToRecyclerView(rv)

        helper.addOnClickListener(R.id.iv_delete)
    }

    class ChildAdapter(layoutResId: Int, data: List<DatePlanBean>?) : BaseQuickAdapter<DatePlanBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DatePlanBean) {
            helper.setText(R.id.tv_start_time, item.startTimeStr)
            helper.setText(R.id.tv_end_time, item.endTimeStr)
            helper.setText(R.id.tv_course, item.course)
            helper.setText(R.id.tv_content, item.content)
            helper.setVisible(R.id.tv_remind,item.isRemindStart||item.isRemindEnd)
        }



    }

}
