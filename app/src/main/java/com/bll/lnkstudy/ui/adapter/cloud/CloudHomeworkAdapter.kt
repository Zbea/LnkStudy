package com.bll.lnkstudy.ui.adapter.cloud

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudHomeworkList
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudHomeworkAdapter(layoutResId: Int, data: List<CloudHomeworkList.CloudHomeworkTypeBean>?) :
    BaseQuickAdapter<CloudHomeworkList.CloudHomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudHomeworkList.CloudHomeworkTypeBean) {
        helper.apply {
            setText(R.id.tv_name, item.name)
            setImageResource(R.id.iv_image, ToolUtils.getImageResId(mContext, item.bgResId))
            setGone(R.id.ll_info,false)
        }

    }

}
