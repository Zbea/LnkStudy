package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CourseBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

//教学科目适配器
class TeachCourseAdapter(layoutResId: Int, data: List<CourseBean>?) : BaseQuickAdapter<CourseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBean) {

        helper.getView<ImageView>(R.id.iv_image).setImageResource(item.imageId)

    }



}
