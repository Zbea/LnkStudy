package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.GlideUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudHomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            val ivImage=getView<ImageView>(R.id.iv_image)
            if (item.state==4){
                setBackgroundRes(R.id.rl_bg,R.drawable.bg_black_stroke_10dp_corner)
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, ivImage, 10)
            }
            else{
                //清除Glide的异步加载任务和缓存，防止残留图片覆盖默认资源
                Glide.with(mContext).clear(ivImage)
                val bg=when(item.state){
                    1->{
                        R.mipmap.icon_homework_cover_1
                    }
                    3->{
                        R.mipmap.icon_homework_cover_3
                    }
                    5->{
                        R.mipmap.icon_homework_cover_5
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
                    9->{
                        R.mipmap.icon_homework_cover_9
                    }
                    10->{
                        R.mipmap.icon_homework_cover_10
                    }
                    11->{
                        R.mipmap.icon_homework_cover_11
                    }
                    else->{
                        R.mipmap.icon_homework_cover_2
                    }
                }
                setImageResource(R.id.iv_image,bg)
                setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                setText(R.id.tv_name, item.name)
            }
            setGone(R.id.ll_info, false)
        }
    }


}
