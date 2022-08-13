package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookAdapter(layoutResId: Int, data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Book) {
        helper.setText(R.id.tv_name,item.name)
        val image=helper.getView<ImageView>(R.id.iv_image)
        if(item.pageUrl.isNullOrEmpty())
        {
            GlideUtils.setImageRoundUrl(mContext,item.assetUrl,image,10)
        }
        else{
            GlideUtils.setImageRoundUrl(mContext,item.pageUrl,image,10)
        }

    }

}
