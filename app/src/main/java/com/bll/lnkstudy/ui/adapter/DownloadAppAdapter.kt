package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppList
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DownloadAppAdapter(layoutResId: Int, data: List<AppList.ListBean>?) : BaseQuickAdapter<AppList.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppList.ListBean) {
        helper.setText(R.id.tv_name,item.nickname)
        helper.setText(R.id.tv_introduce,item.introduction)
        helper.setText(R.id.tv_price,if (item.price==0)"免费" else item.price.toString())
        helper.setGone(R.id.tv_price_title,item.price!=0)
        helper.setText(R.id.btn_download,if (item.buyStatus==0) "购买" else "下载")
        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext,item.assetUrl,image,5)
    }

}