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
            setText(R.id.tv_name,item.typeName)
            setText(R.id.tv_course,DataBeanManager.getCourseStr(item.subject))
            setText(R.id.tv_date, DateUtils.longToStringWeek(item.time))
            setText(R.id.tv_content,item.title)
        }
    }

}
