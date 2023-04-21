package com.bll.lnkstudy.ui.adapter.cloud

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.cloud.CloudPaintingList
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudPaintingAdapter(layoutResId: Int,data: List<CloudPaintingList.PaintingListBean>?) : BaseQuickAdapter<CloudPaintingList.PaintingListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CloudPaintingList.PaintingListBean) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setGone(R.id.tv_price,false)
            setGone(R.id.btn_download,false)
            setText(R.id.tv_author,mContext.getString(R.string.author)+"：${item.author}")
            setText(R.id.tv_introduce,mContext.getString(R.string.introduction)+"：${item.drawDesc}")
            val image=getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,5)
        }
    }

}
