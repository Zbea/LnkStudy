package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkNoticeAdapter(layoutResId: Int, data: List<HomeworkNoticeList.HomeworkNoticeBean>?) : BaseQuickAdapter<HomeworkNoticeList.HomeworkNoticeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkNoticeList.HomeworkNoticeBean) {
        helper.apply {
            setText(R.id.tv_name,item.typeName)
            setText(R.id.tv_course,DataBeanManager.getCourseStr(item.subject))
            setText(R.id.tv_assign_date, "布置时间："+DateUtils.longToStringNoYear(item.time))
            if (item.endTime>0&&DateUtils.date10ToDate13(item.endTime)>=System.currentTimeMillis()){
                setText(R.id.tv_date, "提交时间："+DateUtils.longToStringNoYear(item.endTime))
            }
            setText(R.id.tv_content,item.title)
        }
    }

}
