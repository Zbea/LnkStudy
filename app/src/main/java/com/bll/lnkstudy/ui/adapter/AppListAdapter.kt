package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.utils.BitmapUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.run {
            item.run{
                setText(R.id.tv_name,appName)
                setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(imageByte))
                setImageResource(R.id.cb_check,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
            }
            addOnClickListener(R.id.ll_name,R.id.iv_image)
        }
    }



}
