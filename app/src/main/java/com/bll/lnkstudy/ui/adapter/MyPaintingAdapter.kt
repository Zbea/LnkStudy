package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyPaintingAdapter(layoutResId: Int, data: List<PaintingBean>?) : BaseQuickAdapter<PaintingBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            val image=getView<ImageView>(R.id.iv_image)
            image.scaleType=ImageView.ScaleType.CENTER_INSIDE
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,5)
        }
    }

}
