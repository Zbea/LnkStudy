package com.bll.lnkstudy.ui.adapter


import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateScheduleEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDateEventScheduleAdapter(layoutResId: Int, data: List<DateScheduleEvent>?) : BaseQuickAdapter<DateScheduleEvent, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateScheduleEvent) {
        helper.setText(R.id.tv_title, item.scheduleTitle)
        helper.setText(R.id.tv_start_date, item.scheduleStartTimeStr)
        helper.setText(R.id.tv_start_end, item.scheduleEndTimeStr)

        val tvTitle=helper.getView<TextView>(R.id.tv_title)
        val tvStartDate=helper.getView<TextView>(R.id.tv_start_date)
        val tvEndDate=helper.getView<TextView>(R.id.tv_start_end)

        val endToStart=item.scheduleEndTime-item.scheduleStartTime
        val nowToStart=System.currentTimeMillis()-item.scheduleStartTime
        if (nowToStart>endToStart)
        {
            tvTitle.setTextColor(mContext.getColor(R.color.black_50) )
            tvStartDate.setTextColor(mContext.getColor(R.color.black_50))
            tvEndDate.setTextColor(mContext.getColor(R.color.black_50))
        }

    }
}
