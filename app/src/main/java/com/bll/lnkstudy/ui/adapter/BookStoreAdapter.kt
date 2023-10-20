package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookStoreAdapter(layoutResId: Int, data: List<BookBean>?) : BaseQuickAdapter<BookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookBean) {

        helper.setText(R.id.tv_name,item.bookName)
//        helper.setText(R.id.tv_price,item.price.toString().trim())
        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,10)
    }



}
