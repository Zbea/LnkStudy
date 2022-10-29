package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppListBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * type=0壁纸 type=1书画
 */
class AppWallpaperListAdapter(layoutResId: Int, data: List<AppListBean.ListBean>?,private val type:Int) : BaseQuickAdapter<AppListBean.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppListBean.ListBean) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_price,""+item.price+"学豆")
        helper.setText(R.id.btn_download,if (item.status==0) "购买" else "下载")
        if (type==1){
            helper.setText(R.id.tv_page,"${item.images.size}页")
        }
        val image=helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext,item.assetUrl,image,5)

        helper.addOnClickListener(R.id.btn_download)
    }

}
