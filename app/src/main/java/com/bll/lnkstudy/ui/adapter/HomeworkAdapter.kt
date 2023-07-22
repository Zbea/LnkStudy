package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setText(R.id.tv_name, item.name)
            setVisible(R.id.ll_info, !item.isCreate||item.isCloud)

            if (item.state==4){
                setBackgroundRes(R.id.iv_image,R.drawable.bg_black_stroke_5dp_corner)
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, getView(R.id.iv_image), 10)
            }
            else{
                setBackgroundRes(R.id.iv_image, ToolUtils.getImageResId(mContext, item.bgResId))
                setImageResource(R.id.iv_image,ToolUtils.getImageResId(mContext, item.bgResId))
            }

            //云作业显示年级
            if (item.isCloud){
                setText(R.id.tv_grade, DataBeanManager.grades[item.grade-1].desc)
                setText(R.id.tv_date, DateUtils.intToStringDataNoHour(item.date))
            }
            else{
                setGone(R.id.tv_grade,false)
                setGone(R.id.tv_date,false)
            }

            if (item.isPg) {
                setTextColor(R.id.tv_pg, mContext.getColor(R.color.black))
                getView<TextView>(R.id.tv_pg).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                setTextColor(R.id.tv_pg, mContext.getColor(R.color.gray))
                getView<TextView>(R.id.tv_pg).typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            if (item.isMessage) {
                setTextColor(R.id.tv_message, mContext.getColor(R.color.black))
                getView<TextView>(R.id.tv_message).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                setTextColor(R.id.tv_message, mContext.getColor(R.color.gray))
                getView<TextView>(R.id.tv_message).typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            if (item.state == 1) {
                setText(R.id.tv_message, mContext.getString(R.string.homework_receiver_reel))
            } else {
                setText(R.id.tv_message, mContext.getString(R.string.homework_receiver_message))
            }

            addOnClickListener(R.id.ll_message)
        }


    }


}
