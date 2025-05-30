package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.SPUtil.getObj
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.Objects

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) : BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    var grade = Objects.requireNonNull(getObj("user", User::class.java))?.grade

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setVisible(R.id.ll_info, !item.isCloud&&(item.createStatus==1||item.createStatus==2))
            setText(R.id.tv_grade,if (grade!=item.grade) "(${DataBeanManager.getGradeStr(item.grade)})" else "" )
            setText(R.id.tv_name, item.name)
            val ivImage=getView<ImageView>(R.id.iv_image)
            when(item.state){
                1->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_3)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                3->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_2)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                4->{
                    setGone(R.id.tv_grade,!item.isCloud)
                    setBackgroundRes(R.id.rl_bg,R.drawable.bg_black_stroke_10dp_corner)
                    setText(R.id.tv_name, "")
                    GlideUtils.setImageRoundUrl(mContext, item.bgResId, ivImage, 10)
                }
                5->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_4)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                6->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_7)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                7->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_5)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                8->{
                    ivImage.setImageResource(R.mipmap.icon_homework_cover_6)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
                else->{
                    ivImage.setImageResource(if (item.name=="作文作业本")R.mipmap.icon_homework_cover_8 else R.mipmap.icon_homework_cover_1)
                    setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
                }
            }

            if (item.isPg) {
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

            addOnClickListener(R.id.ll_message)
        }


    }


}
