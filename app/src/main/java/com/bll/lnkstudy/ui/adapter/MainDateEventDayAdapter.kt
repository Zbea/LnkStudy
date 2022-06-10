package com.bll.lnkstudy.ui.adapter


import android.view.View
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateDayEvent
import com.bll.lnkstudy.utils.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class MainDateEventDayAdapter(layoutResId: Int, data: List<DateDayEvent>?) : BaseQuickAdapter<DateDayEvent, BaseViewHolder>(layoutResId, data) {

    private var nowLong:Long?= StringUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))

    lateinit var onConfirm: (View, Int) -> Unit

    override fun convert(helper: BaseViewHolder, item: DateDayEvent) {
        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_date, item.dayStr)

        val day= StringUtils.sublongToDay(item.dayLong, nowLong!!)

        helper.setText(R.id.tv_countdown, "$day 天后")
        helper.setVisible(R.id.tv_countdown,day>0)
    }

    //获得当前时间 用于计算剩余天数
    fun setDateLong(long: Long){
        nowLong=long
    }
}
