package com.bll.lnkstudy.ui.adapter


import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.CourseSelectDialog
import com.bll.lnkstudy.dialog.DateTimeSelectorDialog
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanEventAddAdapter(layoutResId: Int, data: List<DatePlanBean>?) :
    BaseQuickAdapter<DatePlanBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlanBean) {
        val tv_start=helper.getView<TextView>(R.id.tv_start_time)
        val tv_end=helper.getView<TextView>(R.id.tv_end_time)
        val ll_time=helper.getView<LinearLayout>(R.id.ll_time)
        val tv_course=helper.getView<TextView>(R.id.tv_course)
        val et_content=helper.getView<EditText>(R.id.et_content)
        val tv_remind=helper.getView<TextView>(R.id.tv_remind)

        tv_start.text=if (TextUtils.isEmpty(item.startTimeStr)) "开始" else item.startTimeStr
        tv_end.text=if (TextUtils.isEmpty(item.endTimeStr)) "结束" else item.endTimeStr
        tv_course.text=if (TextUtils.isEmpty(item.course)) "科目" else item.course
        et_content.setText(item.content)

        helper.getView<EditText>(R.id.et_content).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                item.content = p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        tv_course.setOnClickListener {
            CourseSelectDialog(mContext).builder().setOnDialogClickListener {
                item.course=it.name
                tv_course.text=it.name
            }
        }
        ll_time.setOnClickListener{
            DateTimeSelectorDialog(mContext,item).builder()
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
            DateTimeSelectorDialog(mContext,item).builder()
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
