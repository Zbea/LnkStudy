package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ListAdapter(layoutResId: Int, data: List<ListBean>?) : BaseQuickAdapter<ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ListBean) {
        helper.setText(R.id.tv_name,item.name)
        val image=helper.getView<ImageView>(R.id.iv_image)

        if (item.url!=null)
            Glide.with(mContext).load(item.url).apply(RequestOptions().skipMemoryCache(false)).into(image)

    }



}
