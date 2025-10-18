package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkNoticeAdapter(layoutResId: Int,val type:Int, data: List<HomeworkNoticeList.HomeworkNoticeBean>?) : BaseQuickAdapter<HomeworkNoticeList.HomeworkNoticeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkNoticeList.HomeworkNoticeBean) {
        helper.apply {
            setText(R.id.tv_name,item.typeName)
            setText(R.id.tv_course,DataBeanManager.getCourseStr(item.subject))
            if (type==0){
                setText(R.id.tv_assign_date, "布置时间："+DateUtils.longToStringWeek(item.time))
                if (item.endTime>0){
                    setText(R.id.tv_date, "提交时间："+DateUtils.longToStringWeek(item.endTime))
                }
            }
            else{
                val startTime=DateUtils.dateStrToLong(item.startTime)
                if (startTime>0){
                    setText(R.id.tv_assign_date, "布置时间："+DateUtils.longToStringWeek(startTime))
                }
                setText(R.id.tv_date, "批改时间："+DateUtils.longToStringWeek(item.time))

                setText(R.id.tv_score, MethodManager.getCorrectNoticeScore(item.score,item.correctJson,item.correctMode))
            }

            setText(R.id.tv_content,item.title)
        }
    }

}
