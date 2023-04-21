package com.bll.lnkstudy.ui.adapter.cloud


import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudExamList.CloudExamTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudExamTypeAdapter(layoutResId: Int, data: List<CloudExamTypeBean>?) : BaseQuickAdapter<CloudExamTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudExamTypeBean) {
        helper.apply {
            setText(R.id.tv_name,item.name)
        }
    }

}
