package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Book
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookStoreAdapter(layoutResId: Int, data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Book) {

        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_price,item.price.toString().trim())
        val image=helper.getView<ImageView>(R.id.iv_image)

        Glide.with(mContext).load(item.assetUrl).apply(RequestOptions().skipMemoryCache(false))
            .into(image)
    }



}
