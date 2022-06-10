package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CourseList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CourseAdapter(layoutResId: Int, data: List<CourseList>?) : BaseQuickAdapter<CourseList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseList) {
        var tvName=helper.getView<TextView>(R.id.tv_name)
        tvName.text=item.name
        if (item.isSelect){
            tvName.typeface=Typeface.defaultFromStyle(Typeface.BOLD)
            tvName.setTextColor(mContext.getColor(R.color.black))
        }
        else{
            tvName.typeface=Typeface.defaultFromStyle(Typeface.NORMAL)
            tvName.setTextColor(mContext.getColor(R.color.black_20))
        }
    }

}
