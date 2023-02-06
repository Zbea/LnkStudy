package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyPaintingAdapter(layoutResId: Int, data: List<PaintingBean>?) : BaseQuickAdapter<PaintingBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingBean) {
        helper.setText(R.id.tv_name,item.title)
        helper.setGone(R.id.tv_price,false)
        helper.setGone(R.id.btn_download,false)
        helper.setText(R.id.tv_author,"作者：${item.author}")
        helper.setText(R.id.tv_introduce,"简介：${item.info}")

        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,5)
    }



}
