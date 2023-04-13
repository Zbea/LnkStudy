package com.bll.lnkstudy.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setText(R.id.tv_name, item.name)
            setText(R.id.tv_grade, DataBeanManager.grades[item.grade-1].desc)
            setImageResource(R.id.iv_image, ToolUtils.getImageResId(mContext, item?.bgResId))
            setVisible(R.id.ll_info, !item.isCreate)

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
                setText(R.id.tv_message, "收到题卷")
            } else {
                setText(R.id.tv_message, "收到通知")
            }

            addOnClickListener(R.id.ll_message)
        }


    }


}
