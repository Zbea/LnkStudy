package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudDiaryAdapter(layoutResId: Int, data: List<CloudListBean>?) : BaseQuickAdapter<CloudListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudListBean) {
        helper.setText(R.id.tv_title,item.subTypeStr)
        helper.setText(R.id.tv_date, DateUtils.longToStringWeek(item.date))
        helper.addOnClickListener(R.id.iv_delete)
    }

}
