package com.bll.lnkstudy.ui.adapter


import android.text.TextUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.DateTimeSelectorDialog
import com.bll.lnkstudy.mvp.model.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanEventAddAdapter(layoutResId: Int, data: List<DatePlan>?) :
    BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlan) {
        val tv_start=helper.getView<TextView>(R.id.tv_start_time)
        val tv_end=helper.getView<TextView>(R.id.tv_end_time)
        val ll_time=helper.getView<LinearLayout>(R.id.ll_time)
        val tv_course=helper.getView<TextView>(R.id.et_course)
        val et_content=helper.getView<EditText>(R.id.et_content)
        val tv_remind=helper.getView<TextView>(R.id.tv_remind)

        tv_start.text=if (TextUtils.isEmpty(item.startTimeStr)) "开始" else item.startTimeStr
        tv_end.text=if (TextUtils.isEmpty(item.endTimeStr)) "结束" else item.endTimeStr
        tv_course.text=item.course
        et_content.setText(item.content)

        et_content.doOnTextChanged { text, start, before, count ->
            item.content = text.toString().trim()
        }

        tv_course.doOnTextChanged { text, start, before, count ->
            item.course = text.toString().trim()
        }

        ll_time.setOnClickListener{
            DateTimeSelectorDialog(mContext,item,0).builder()
                .setOnDateListener { startStr, endStr,isRemindStart,isRemindEnd ->
                    item.startTimeStr=startStr
                    tv_start.text=startStr
                    item.isRemindStart=isRemindStart

                    item.endTimeStr=endStr
                    tv_end.text=endStr
                    item.isRemindEnd=isRemindEnd
                }
        }
        tv_remind.setOnClickListener{
            DateTimeSelectorDialog(mContext,item,1).builder()
                .setOnDateListener { startStr, endStr,isRemindStart,isRemindEnd ->
                    item.startTimeStr=startStr
                    tv_start.text=startStr
                    item.isRemindStart=isRemindStart

                    item.endTimeStr=endStr
                    tv_end.text=endStr
                    item.isRemindEnd=isRemindEnd
                }
        }

    }


}
