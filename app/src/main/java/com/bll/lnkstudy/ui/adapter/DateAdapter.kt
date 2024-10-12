package com.bll.lnkstudy.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.defaultFromStyle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DateAdapter(layoutResId: Int, data: List<DateBean>?) :
    BaseQuickAdapter<DateBean, BaseViewHolder>(layoutResId, data) {

    @SuppressLint("WrongConstant")
    override fun convert(helper: BaseViewHolder, item: DateBean) {
        helper.apply {
            item.apply {
                val tvDay = getView<TextView>(R.id.tv_day)
                val tvLunar=getView<TextView>(R.id.tv_lunar)
                val ivImage=helper.getView<ImageView>(R.id.iv_image)
                val rlImage=helper.getView<RelativeLayout>(R.id.rl_image)

                tvDay.text = if (day == 0) "" else day.toString()
                if (isNow)
                    tvDay.typeface = defaultFromStyle(BOLD)
                val str = if (!solar.solar24Term.isNullOrEmpty()) {
                    solar.solar24Term
                } else {
                    if (!solar.solarFestivalName.isNullOrEmpty()) {
                        solar.solarFestivalName
                    } else {
                        if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                            lunar.lunarFestivalName
                        } else {
                            lunar.getChinaDayString(lunar.lunarDay)
                        }
                    }
                }
                tvLunar.text=str

//                if (item.time!=0L){
//                    val path= FileAddress().getPathDate(DateUtils.longToStringCalender(item.time))+"/draw.png"
//                    if (File(path).exists()){
//                        GlideUtils.setImageFileNoCache(mContext,File(path),ivImage)
//                        rlImage.visibility= View.VISIBLE
//                    }
//                    else{
//                        rlImage.visibility= View.GONE
//                    }
//                }
//                else{
//                    rlImage.visibility= View.GONE
//                }
            }
        }

    }

}
