package com.bll.lnkstudy.ui.adapter


import android.widget.CheckBox
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanWeekAdapter(layoutResId: Int, data: List<DateWeek>?) :
    BaseQuickAdapter<DateWeek, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateWeek) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setChecked(R.id.cb_week,item.isCheck)
            getView<CheckBox>(R.id.cb_week).setOnClickListener{
                item.isCheck=!item.isCheck
                notifyDataSetChanged()
            }
        }
    }
}
