package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CourseBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainTextBookAdapter(layoutResId: Int, data: List<CourseBean>?) : BaseQuickAdapter<CourseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBean) {
        val image=helper.getView<ImageView>(R.id.iv_image)
        image.setImageResource(item.mainCourseId)

    }



}
