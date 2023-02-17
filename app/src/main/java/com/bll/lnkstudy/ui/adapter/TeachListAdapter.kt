package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 教学内容适配器
 */
class TeachListAdapter(layoutResId: Int, data: List<TeachingVideoList.ItemBean>?) : BaseQuickAdapter<TeachingVideoList.ItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeachingVideoList.ItemBean) {
        item.run {
            helper.run {
                setText(R.id.tv_name,videoName)
                setText(R.id.tv_info,videoDesc)
                val image=getView<ImageView>(R.id.iv_image)
                GlideUtils.setImageRoundUrl(mContext,imageUrl,image,5)
            }
        }
    }

}
