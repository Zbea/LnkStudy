package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.mvp.model.WallpaperBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyPaintingAdapter(layoutResId: Int, data: List<WallpaperBean>?) : BaseQuickAdapter<WallpaperBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: WallpaperBean) {
        helper.setText(R.id.tv_name,item.title)
        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageUrl(mContext,item.imageUrl,image)
    }



}
