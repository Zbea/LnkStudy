package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class MainDateEventDayAdapter(layoutResId: Int, data: List<DateEvent>?) : BaseQuickAdapter<DateEvent, BaseViewHolder>(layoutResId, data) {

    private var nowLong= DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))

    override fun convert(helper: BaseViewHolder, item: DateEvent) {
        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_date, item.dayLongStr)

        val day= DateUtils.sublongToDay(item.dayLong, nowLong!!)

        helper.setText(R.id.tv_countdown, "$day 天后")
        helper.setVisible(R.id.tv_countdown,day>0)
    }

    //获得当前时间 用于计算剩余天数
    fun setDateLong(long: Long){
        nowLong=long
    }
}
