package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.painting.PaintingList
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DownloadPaintingAdapter(layoutResId: Int, data: List<PaintingList.ListBean>?) : BaseQuickAdapter<PaintingList.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingList.ListBean) {
        helper.apply {
            setText(R.id.tv_name,item.drawName)
            setText(R.id.tv_price,getString(R.string.price)+"："+if (item.price==0) getString(R.string.free) else "${item.price}"+getString(R.string.xd))
            setText(R.id.btn_download,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
            setText(R.id.tv_author,getString(R.string.author)+"：${item.author}")
            setText(R.id.tv_publishers,getString(R.string.publisher)+"：${item.publisher}")
            setText(R.id.tv_introduce,getString(R.string.introduction)+"：${item.drawDesc}")

            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,getView(R.id.iv_image),10)

            addOnClickListener(R.id.btn_download)
        }
    }

    fun getString(resId:Int):String{
        return mContext.getString(resId)
    }

}
