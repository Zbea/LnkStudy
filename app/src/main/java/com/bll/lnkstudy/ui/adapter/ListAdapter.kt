package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ListAdapter(layoutResId: Int, data: List<ListBean>?) : BaseQuickAdapter<ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ListBean) {
        helper.setText(R.id.tv_name,item.name)
        val image=helper.getView<ImageView>(R.id.iv_image)

        if (item.url!=null)
            GlideUtils.setImageUrl(mContext,item.url,image)


    }



}
