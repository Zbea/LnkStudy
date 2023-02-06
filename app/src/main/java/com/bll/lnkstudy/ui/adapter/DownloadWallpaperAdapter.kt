package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PaintingList
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * type=0壁纸 type=1书画
 */
class DownloadWallpaperAdapter(layoutResId: Int, data: List<PaintingList.ListBean>?) : BaseQuickAdapter<PaintingList.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingList.ListBean) {
        helper.setText(R.id.tv_name,item.drawName)
        helper.setText(R.id.tv_price,if (item.price==0)"免费" else item.price.toString())
        helper.setGone(R.id.tv_price_title,item.price!=0)
        helper.setText(R.id.btn_download,if (item.buyStatus==1) "下载" else "购买")
        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,5)

        helper.addOnClickListener(R.id.btn_download)
    }

}
