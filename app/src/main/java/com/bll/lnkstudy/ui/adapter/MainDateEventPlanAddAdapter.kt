package com.bll.lnkstudy.ui.adapter


import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DatePlanBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.bll.lnkstudy.dialog.DateTimeHourDialog

class MainDateEventPlanAddAdapter(layoutResId: Int, data: List<DatePlanBean>?) :
    BaseQuickAdapter<DatePlanBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlanBean) {
        var tv_start=helper.getView<TextView>(R.id.tv_start_time)
        var tv_end=helper.getView<TextView>(R.id.tv_end_time)
        var et_view=helper.getView<EditText>(R.id.et_content)

        tv_start.text=if (TextUtils.isEmpty(item.startTimeStr)) "开始" else item.startTimeStr
        tv_end.text=if (TextUtils.isEmpty(item.endTimeStr)) "结束" else item.endTimeStr
        et_view.setText(item.content)

        helper.getView<EditText>(R.id.et_content).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                item.content = p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        tv_start.setOnClickListener {
            DateTimeHourDialog(mContext).builder()
                .setDialogClickListener(object : DateTimeHourDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        tv_start.text= DateUtils.longToHour(dateTim)
                        item.startTime = dateTim
                        item.startTimeStr = DateUtils.longToHour(dateTim)
                    }
                })
        }
        tv_end.setOnClickListener {
            DateTimeHourDialog(mContext).builder()
                .setDialogClickListener(object : DateTimeHourDialog.DateListener {
                    override fun getDate(dateStr: String?, hourStr: String?, dateTim: Long) {
                        tv_end.text= DateUtils.longToHour(dateTim)
                        item.endTime = dateTim
                        item.endTimeStr = DateUtils.longToHour(dateTim)
                    }
                })
        }

    }
}
