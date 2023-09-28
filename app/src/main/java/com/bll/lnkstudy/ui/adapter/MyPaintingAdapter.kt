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
            setGone(R.id.tv_price,false)
            setGone(R.id.btn_download,false)
            setText(R.id.tv_author,mContext.getString(R.string.author)+"：${item.author}")
            setText(R.id.tv_introduce,mContext.getString(R.string.introduction)+"：${item.info}")
            setText(R.id.tv_publishers,mContext.getString(R.string.publisher)+"：${item.publisher}")
            val image=getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,5)
        }
    }

}
