package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class DateDayListAdapter(layoutResId: Int, data: List<DateEventBean>?) : BaseQuickAdapter<DateEventBean, BaseViewHolder>(layoutResId, data) {

    private var nowLong= DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))

    override fun convert(helper: BaseViewHolder, item: DateEventBean) {
        helper.apply {
            setText(R.id.tv_title, item.title)
            setText(R.id.tv_date, item.dayLongStr)
            setText(R.id.tv_remind, item.remindDay.toString())

            val day= DateUtils.sublongToDay(item.dayLong, nowLong!!)
            if (day>=0){
                setText(R.id.tv_countdown, day.toString())
            }
            else{
                setTextColor(R.id.tv_title,mContext.getColor(R.color.gray) )
                setTextColor(R.id.tv_date,mContext.getColor(R.color.gray) )
            }

            setVisible(R.id.ll_countdown,day>0&&item.isCountdown)
            setVisible(R.id.ll_remind,day>0&&item.isRemind)
            addOnClickListener(R.id.iv_delete)
        }
    }
}
