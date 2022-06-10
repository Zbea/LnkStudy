package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Book
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainTextBookAdapter(layoutResId: Int, data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Book) {
        val image=helper.getView<ImageView>(R.id.iv_image)
        if(item.pageUrl.isNullOrEmpty())
        {
            Glide.with(mContext).load(item.assetUrl).thumbnail(0.1f).into(image)
        }
        else{
            Glide.with(mContext).load(item.pageUrl).thumbnail(0.1f).into(image)
        }



    }



}
