package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TextBookAdapter(layoutResId: Int, data: List<BookBean>?) :
    BaseQuickAdapter<BookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookBean) {
        helper.apply {
            setText(R.id.tv_name, item.bookName)
            val image = getView<ImageView>(R.id.iv_image)
            if (item.pageUrl.isNullOrEmpty()) {
                GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 10)
            } else {
                GlideUtils.setImageRoundUrl(mContext, item.pageUrl, image, 10)
            }
            setVisible(R.id.iv_lock, item.category==0&&item.dateState == 1 && item.isLock)
        }
    }

}
