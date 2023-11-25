package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudHomeworkAdapter(layoutResId: Int, data: List<HomeworkTypeBean>?) :
    BaseQuickAdapter<HomeworkTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkTypeBean) {
        helper.apply {
            if (item.state==4){
                setBackgroundRes(R.id.iv_image,R.drawable.bg_black_stroke_5dp_corner)
                GlideUtils.setImageRoundUrl(mContext, item.bgResId, getView(R.id.iv_image), 10)
            }
            else{
                setText(R.id.tv_name, item.name)
                setText(R.id.tv_date,DateUtils.intToStringDataNoHour(item.date))

                setImageResource(R.id.iv_image,R.color.color_transparent)
                setBackgroundRes(R.id.iv_image,ToolUtils.getImageResId(mContext, item.bgResId))
            }
            setGone(R.id.ll_info, false)
        }
    }


}
