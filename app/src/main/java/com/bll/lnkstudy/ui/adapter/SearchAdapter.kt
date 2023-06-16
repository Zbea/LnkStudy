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
            2->{
                helper.setText(R.id.tv_course,item.course)
                helper.setText(R.id.tv_type,item.typeStr)
                helper.setImageResource(R.id.iv_image,R.mipmap.icon_search_homework_bg)
            }
            3->{
                helper.setText(R.id.tv_course,item.course)
                helper.setText(R.id.tv_type,item.typeStr)
                helper.setImageResource(R.id.iv_image,R.mipmap.icon_search_exam_bg)
            }
            4->{
                helper.setText(R.id.tv_course,item.typeStr)
                helper.setText(R.id.tv_type,item.noteStr)
                helper.setImageResource(R.id.iv_image,R.mipmap.icon_search_note_bg)
            }
        }

    }

}