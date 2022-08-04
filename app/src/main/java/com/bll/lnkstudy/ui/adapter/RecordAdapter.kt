package com.bll.lnkstudy.ui.adapter

import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.utils.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class RecordAdapter(layoutResId: Int, data: MutableList<RecordBean>?) : BaseQuickAdapter<RecordBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: RecordBean) {

        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, StringUtils.longToStringDataNoHour(item.date))

        var image=helper.getView<ImageView>(R.id.iv_record)
        if (item.state==0){
            image.setImageResource(R.mipmap.icon_record_play)
        }else{
            image.setImageResource(R.mipmap.icon_record_pause)
        }

        helper.addOnClickListener(R.id.iv_record)
        helper.addOnClickListener(R.id.iv_setting)
    }
}
