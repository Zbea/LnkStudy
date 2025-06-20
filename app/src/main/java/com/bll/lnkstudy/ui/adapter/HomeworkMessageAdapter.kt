package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkMessageAdapter(layoutResId: Int, data: MutableList<HomeworkMessageList.MessageBean>, private val createStatus: Int) : BaseQuickAdapter<HomeworkMessageList.MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkMessageList.MessageBean) {
        if (createStatus==2){
            helper.setText(R.id.tv_title,item.title)
            helper.setText(R.id.tv_assign_date, "布置时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
            if (item.submitState==0){
                helper.setGone(R.id.tv_correct,item.selfBatchStatus==1)
                helper.setGone(R.id.tv_standardTime,item.minute>0)
                helper.setText(R.id.tv_standardTime,"${item.minute}分钟")
                helper.setText(R.id.tv_end_date, "提交时间："+DateUtils.longToStringWeek(item.endTime))
            }
            else{
                helper.setText(R.id.tv_end_date, "不提交")
            }
        }
        else{
            helper.setText(R.id.tv_title,item.title)
            helper.setGone(R.id.tv_standardTime,false)
            helper.setGone(R.id.tv_correct,false)
            helper.setText(R.id.tv_assign_date, "布置时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
            helper.setText(R.id.tv_end_date, "提交时间："+DateUtils.longToStringWeek(item.endTime))
        }
    }

}
