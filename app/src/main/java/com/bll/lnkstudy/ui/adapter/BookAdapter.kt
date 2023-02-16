package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookAdapter(layoutResId: Int, data: List<BookBean>?) : BaseQuickAdapter<BookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookBean) = item.run{
        helper.run {
            setText(R.id.tv_name,bookName)
            val image=getView<ImageView>(R.id.iv_image)
            if(pageUrl.isNullOrEmpty()) {
                GlideUtils.setImageRoundUrl(mContext,imageUrl,image,10)
            } else{
                GlideUtils.setImageRoundUrl(mContext,pageUrl,image,10)
            }
        }
    }

}
