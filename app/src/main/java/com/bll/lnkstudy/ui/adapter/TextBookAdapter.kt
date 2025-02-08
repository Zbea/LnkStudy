package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.textbook.TextbookBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TextBookAdapter(layoutResId: Int, data: List<TextbookBean>?) :
    BaseQuickAdapter<TextbookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TextbookBean) {
        helper.setText(R.id.tv_name, item.bookName)
        val image = helper.getView<ImageView>(R.id.iv_image)
        if (item.pageUrl.isNullOrEmpty()) {
            GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 8)
        } else {
            GlideUtils.setImageRoundUrl(mContext, item.pageUrl, image, 8)
        }
        helper.setVisible(R.id.iv_lock, item.isLock)
    }

}
