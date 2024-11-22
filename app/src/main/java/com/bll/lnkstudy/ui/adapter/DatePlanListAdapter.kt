package com.bll.lnkstudy.ui.adapter


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanListAdapter(layoutResId: Int, data: List<DateEventBean>?) : BaseQuickAdapter<DateEventBean, BaseViewHolder>(layoutResId, data) {


    override fun convert(helper: BaseViewHolder, item: DateEventBean) {
        helper.apply {
            setText(R.id.tv_title, item.title)
            var weekStr=""
            if (item.date==0){
                for (week in item.weeks)
                {
                    weekStr += "${week.name}  "
                }
            }
            else{
                for (date in item.dates)
                {
                    weekStr += "${DateUtils.longToStringDataNoYear(date)}  "
                }
            }
            setText(R.id.tv_week,weekStr)
            getView<RecyclerView>(R.id.rv_list).apply {
                layoutManager = LinearLayoutManager(mContext)//创建布局管理
                val childAdapter= MainDatePlanAdapter(R.layout.item_date_plan_list_child, item.plans)
                adapter = childAdapter
                childAdapter.bindToRecyclerView(this)
            }
            addOnClickListener(R.id.iv_delete)
        }
    }

}
