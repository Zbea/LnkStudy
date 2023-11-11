package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PastHomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) : BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            setText(R.id.tv_name, item.name)
            setGone(R.id.ll_info, false)
            setText(R.id.tv_grade, DataBeanManager.getGradeStr(item.grade))
            setText(R.id.tv_date, DateUtils.intToStringDataNoHour(item.date))
            val ivImage=getView<ImageView>(R.id.iv_image)
            if (item.state==4){
                setBackgroundRes(R.id.iv_image,R.drawable.bg_black_stroke_5dp_corner)
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, ivImage, 10)
            }
            else{
                setImageResource(R.id.iv_image,R.color.color_transparent)
                setBackgroundRes(R.id.iv_image,ToolUtils.getImageResId(mContext, item.bgResId))
            }
        }


    }


}
