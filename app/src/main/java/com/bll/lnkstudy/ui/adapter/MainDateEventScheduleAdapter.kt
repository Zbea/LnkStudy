package com.bll.lnkstudy.ui.adapter


import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainDateEventScheduleAdapter(layoutResId: Int, data: List<DateEvent>?) : BaseQuickAdapter<DateEvent, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateEvent) {
        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_start_date, item.startTimeStr)
        helper.setText(R.id.tv_start_end, item.endTimeStr)

        val tvTitle=helper.getView<TextView>(R.id.tv_title)
        val tvStartDate=helper.getView<TextView>(R.id.tv_start_date)
        val tvEndDate=helper.getView<TextView>(R.id.tv_start_end)

        val endToStart=item.endTime-item.startTime
        val nowToStart=System.currentTimeMillis()-item.startTime
        if (nowToStart>endToStart)
        {
            tvTitle.setTextColor(mContext.getColor(R.color.black_50) )
            tvStartDate.setTextColor(mContext.getColor(R.color.black_50))
            tvEndDate.setTextColor(mContext.getColor(R.color.black_50))
        }
        else{
            tvTitle.setTextColor(mContext.getColor(R.color.black) )
            tvStartDate.setTextColor(mContext.getColor(R.color.black_20))
            tvEndDate.setTextColor(mContext.getColor(R.color.black_20))
        }

    }
}
