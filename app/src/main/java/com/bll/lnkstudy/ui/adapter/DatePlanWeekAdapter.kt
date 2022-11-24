package com.bll.lnkstudy.ui.adapter


import android.widget.CheckBox
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateWeekBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanWeekAdapter(layoutResId: Int, data: List<DateWeekBean>?) :
    BaseQuickAdapter<DateWeekBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DateWeekBean) {

        helper.setText(R.id.tv_name,item.name)
        helper.setChecked(R.id.cb_week,item.isCheck)
        helper.getView<CheckBox>(R.id.cb_week).setOnClickListener{
            item.isCheck=!item.isCheck
            notifyDataSetChanged()
        }
    }
}
