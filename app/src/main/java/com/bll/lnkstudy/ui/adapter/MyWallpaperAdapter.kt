package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyWallpaperAdapter(layoutResId: Int, data: List<PaintingBean>?) : BaseQuickAdapter<PaintingBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            GlideUtils.setImageUrl(mContext,item.imageUrl,getView(R.id.iv_image))
            setChecked(R.id.cb_left,item.isLeft)
            setChecked(R.id.cb_right,item.isRight)
            addOnClickListener(R.id.cb_left,R.id.cb_right)
        }
    }

}
