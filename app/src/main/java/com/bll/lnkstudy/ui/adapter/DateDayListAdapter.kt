package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class DateDayListAdapter(layoutResId: Int, data: List<DateEvent>?) : BaseQuickAdapter<DateEvent, BaseViewHolder>(layoutResId, data) {

    private var nowLong= DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))

    override fun convert(helper: BaseViewHolder, item: DateEvent) {
        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_date, item.dayLongStr)
        helper.setText(R.id.tv_remind, item.remindDay.toString())

        val day= DateUtils.sublongToDay(item.dayLong, nowLong!!)
        if (day>0){
            helper.setText(R.id.tv_countdown, day.toString())
        }
        else{
            helper.setTextColor(R.id.tv_title,mContext.getColor(R.color.gray) )
            helper.setTextColor(R.id.tv_date,mContext.getColor(R.color.gray) )
        }

        helper.setVisible(R.id.ll_countdown,day>0&&item.isCountdown)
        helper.setVisible(R.id.ll_remind,day>0&&item.isRemind)

    }
}
