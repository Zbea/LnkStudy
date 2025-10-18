package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.GlideUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) : BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setVisible(R.id.ll_info, !item.isCloud&&(item.createStatus==1||item.createStatus==2))
            setText(R.id.tv_grade,if (MethodManager.getUser().grade!=item.grade) "(${DataBeanManager.getGradeStr(item.grade)})" else "" )

            val ivImage=getView<ImageView>(R.id.iv_image)
            if (item.state==4){
                setGone(R.id.tv_grade,!item.isCloud)
                setText(R.id.tv_name, "")
                setBackgroundRes(R.id.rl_bg,R.drawable.bg_black_stroke_10dp_corner)
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, ivImage, 10)
            }
            else{
                //清除Glide的异步加载任务和缓存，防止残留图片覆盖默认资源
                Glide.with(mContext).clear(ivImage)
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
                    10->{
                        R.mipmap.icon_homework_cover_10
                    }
                    else->{
                        if (item.name=="作文作业本")R.mipmap.icon_homework_cover_5 else R.mipmap.icon_homework_cover_1
                    }
                }
                setImageResource(R.id.iv_image,bg)
                setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                setText(R.id.tv_name, item.name)
            }

            if (item.isCorrect) {
                setTextColor(R.id.tv_pg, mContext.getColor(R.color.black))
                getView<TextView>(R.id.tv_pg).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                setTextColor(R.id.tv_pg, mContext.getColor(R.color.black_50))
                getView<TextView>(R.id.tv_pg).typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            if (item.isMessage) {
                setTextColor(R.id.tv_message, mContext.getColor(R.color.black))
                getView<TextView>(R.id.tv_message).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                setTextColor(R.id.tv_message, mContext.getColor(R.color.black_50))
                getView<TextView>(R.id.tv_message).typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            setGone(R.id.iv_tips,item.isShare)
            addOnClickListener(R.id.ll_message)
        }


    }


}
