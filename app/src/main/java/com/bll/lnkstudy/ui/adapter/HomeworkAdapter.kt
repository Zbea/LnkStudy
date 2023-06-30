package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setText(R.id.tv_name, item.name)
            setImageResource(R.id.iv_image, ToolUtils.getImageResId(mContext, item.bgResId))
            setVisible(R.id.ll_info, !item.isCreate||item.isCloud)
            //题卷本显示年级或者云作业显示年级
            if (item.state==4||item.isCloud){
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
