package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppDownloadListAdapter(layoutResId: Int, data: List<AppBean.ListBean>?) : BaseQuickAdapter<AppBean.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean.ListBean) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_price,""+item.price+"学豆")
        helper.setText(R.id.btn_download,if (item.status==0) "购买" else "下载")
        val image=helper.getView<ImageView>(R.id.iv_image)
        Glide.with(mContext).load(item.assetUrl).thumbnail(0.1f).into(image)
    }



}
