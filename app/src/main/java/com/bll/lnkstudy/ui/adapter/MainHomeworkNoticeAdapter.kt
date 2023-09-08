package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainHomeworkNoticeAdapter(layoutResId: Int, data: List<HomeworkNoticeList.HomeworkNoticeBean>?) : BaseQuickAdapter<HomeworkNoticeList.HomeworkNoticeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkNoticeList.HomeworkNoticeBean) {
        helper.apply {
            setText(R.id.tv_name,item.name+"  "+item.typeName)
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.time))
            if (item.subject>0)
                setText(R.id.tv_course,DataBeanManager.courses[item.subject-1].desc)
            setText(R.id.tv_content,item.title)
            if (item.endTime>0&&item.endTime<System.currentTimeMillis()){
                setText(R.id.tv_end_date, DateUtils.longToStringWeek(item.time)+"之前提交")
            }
        }
    }

}
