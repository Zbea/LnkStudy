package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.defaultFromStyle
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DateAdapter(layoutResId: Int, data: List<DateBean>?) :
    BaseQuickAdapter<DateBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateBean) {
        var tvDay = helper.getView<TextView>(R.id.tv_day)
        var tvLunar=helper.getView<TextView>(R.id.tv_lunar)
        tvDay.text = if (item.day == 0) "" else item.day.toString()
        if (item.isNow)
            tvDay.typeface = defaultFromStyle(BOLD)
        if (item.isNowMonth) {
            tvDay.setTextColor(mContext.getColor(R.color.black))
            tvLunar.setTextColor(mContext.getColor(R.color.gray))
        } else {
            tvDay.setTextColor(mContext.getColor(R.color.black_90))
            tvLunar.setTextColor(mContext.getColor(R.color.black_90))
        }

        val str = if (!item.solar.solar24Term.isNullOrEmpty()) {
            item.solar.solar24Term
        } else {
            if (!item.solar.solarFestivalName.isNullOrEmpty()) {
                item.solar.solarFestivalName
            } else {
                if (!item.lunar.lunarFestivalName.isNullOrEmpty()) {
                    item.lunar.lunarFestivalName
                } else {
                    item.lunar.getChinaDayString(item.lunar.lunarDay)
                }
            }
        }
        tvLunar.text=str

    }

}
