package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.SearchBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class SearchAdapter(layoutResId: Int, data: List<SearchBean>?) : BaseQuickAdapter<SearchBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: SearchBean) {
        helper.setText(R.id.tv_name,item.title)
        when(item.category){
            0,1->{
                GlideUtils.setImageRoundUrl(mContext,item.imageUrl,helper.getView<ImageView>(R.id.iv_image),10)
            }
            4->{
                helper.setText(R.id.tv_course,item.typeStr)
                helper.setText(R.id.tv_type,item.noteStr)
            }
            else->{
                helper.setText(R.id.tv_course,item.course)
                helper.setText(R.id.tv_type,item.typeStr)
            }
        }

    }

}