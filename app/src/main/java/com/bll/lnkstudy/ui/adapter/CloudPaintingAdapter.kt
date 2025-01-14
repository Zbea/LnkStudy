package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudPaintingAdapter(layoutResId: Int, data: List<CloudListBean>?) : BaseQuickAdapter<CloudListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudListBean) {
        helper.apply {
            setImageResource(R.id.iv_painting,if (item.subTypeStr=="我的画本") R.mipmap.icon_painting_list_hb else R.mipmap.icon_painting_list_sf)
            setText(R.id.tv_grade, DataBeanManager.getGradeStr(item.grade))
        }
    }

}
