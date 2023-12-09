package com.bll.lnkstudy.ui.adapter


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanWeekAdapter(layoutResId: Int, data: List<DateWeek>?) :
    BaseQuickAdapter<DateWeek, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateWeek) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            if (item.isSelected){
               setEnabled(R.id.cb_week,false)
               setImageResource(R.id.cb_week,R.mipmap.icon_check_focuse)
            }
            else{
                setEnabled(R.id.cb_week,true)
                setImageResource(R.id.cb_week,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
                addOnClickListener(R.id.cb_week)
            }
        }
    }
}
