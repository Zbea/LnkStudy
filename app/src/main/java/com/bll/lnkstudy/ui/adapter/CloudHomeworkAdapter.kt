package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudHomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            if (item.state==4){
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, getView(R.id.iv_image), 10)
                setBackgroundRes(R.id.rl_bg,R.drawable.bg_black_stroke_10dp_corner)
            }
            else{
                setText(R.id.tv_name, item.name)
                val bg=when(item.state){
                    1->{
                        R.mipmap.icon_homework_cover_3
                    }
                    3->{
                        R.mipmap.icon_homework_cover_2
                    }
                    5->{
                        R.mipmap.icon_homework_cover_4
                    }
                    6->{
                        R.mipmap.icon_homework_cover_6
                    }
                    7->{
                        R.mipmap.icon_homework_cover_7
                    }
                    8->{
                        R.mipmap.icon_homework_cover_8
                    }
                    else->{
                        if (item.name=="作文作业本")R.mipmap.icon_homework_cover_5 else R.mipmap.icon_homework_cover_1
                    }
                }
                setImageResource(R.id.iv_image,bg)
                setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
            }
            setGone(R.id.ll_info, false)
        }
    }


}
