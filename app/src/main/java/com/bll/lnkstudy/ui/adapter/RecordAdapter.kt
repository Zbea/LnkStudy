package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class RecordAdapter(layoutResId: Int, data: MutableList<RecordBean>?) : BaseQuickAdapter<RecordBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: RecordBean) {
        helper.apply {
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoHour(item.date))
            setGone(R.id.tv_result,item.isCorrect)
            setImageResource(R.id.iv_record,if (item.state==0) R.mipmap.icon_record_play else R.mipmap.icon_record_pause)
            addOnClickListener(R.id.iv_record,R.id.iv_delete,R.id.iv_edit,R.id.tv_result)
        }
    }
}
