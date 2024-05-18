package com.bll.lnkstudy.ui.adapter


import android.graphics.Typeface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DatePlanEventAddAdapter(layoutResId: Int, data: List<DatePlan>?) :
    BaseQuickAdapter<DatePlan, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DatePlan) {
        helper.setText(R.id.tv_course,item.course)
        val tv_start=helper.getView<TextView>(R.id.tv_start_time)
        val tv_end=helper.getView<TextView>(R.id.tv_end_time)
        tv_start.text=if (TextUtils.isEmpty(item.startTimeStr)) mContext.getString(R.string.start) else item.startTimeStr
        tv_end.text=if (TextUtils.isEmpty(item.endTimeStr)) mContext.getString(R.string.end) else item.endTimeStr

        tv_start.setTypeface(null,if (item.isStartSelect) Typeface.BOLD else Typeface.NORMAL)
        tv_end.setTypeface(null,if (item.isEndSelect) Typeface.BOLD else Typeface.NORMAL)

        helper.setText(R.id.tv_course,item.course)
        val et_content=helper.getView<EditText>(R.id.et_content)
        et_content.setText(item.content)
        val textWatcher=object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                item.content = p0.toString().trim()
            }

        }
//        et_content.doOnTextChanged { text, start, before, count ->
//            item.content = text.toString().trim()
//        }

        et_content.setOnFocusChangeListener{_,hasFocus->
            if (hasFocus){
                et_content.addTextChangedListener(textWatcher)
            }else{
                et_content.removeTextChangedListener(textWatcher)
            }
        }


//        ll_time.setOnClickListener{
//            DateTimeSelectorDialog(mContext,item,0).builder()
//                .setOnDateListener { startStr, endStr ->
//                    item.startTimeStr=startStr
//                    tv_start.text=startStr
//
//                    item.endTimeStr=endStr
//                    tv_end.text=endStr
//                }
//        }

        helper.addOnClickListener(R.id.tv_course,R.id.tv_start_time,R.id.tv_end_time)

    }


}
