package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.book.TeachingMaterialList
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DocumentListAdapter(layoutResId: Int, data: List<TeachingMaterialList.TeachingMaterialBean>?) : BaseQuickAdapter<TeachingMaterialList.TeachingMaterialBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeachingMaterialList.TeachingMaterialBean) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_teacher,item.teacherName)
        helper.setText(R.id.tv_course,DataBeanManager.getCourseStr(item.subject))
        helper.setText(R.id.tv_time,DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime) ))
    }

}
