package com.bll.lnkstudy.ui.adapter.cloud

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudPaintingList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudPaintingLocalAdapter(layoutResId: Int, data: List<CloudPaintingList.PaintingListBean>?) : BaseQuickAdapter<CloudPaintingList.PaintingListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudPaintingList.PaintingListBean) {
        helper.apply {
            setImageResource(R.id.iv_painting,if (item.localType==0) R.mipmap.icon_painting_sm else R.mipmap.icon_painting_yb)
            setText(R.id.tv_grade, DataBeanManager.grades[item.grade-1].desc)
        }
    }

}
