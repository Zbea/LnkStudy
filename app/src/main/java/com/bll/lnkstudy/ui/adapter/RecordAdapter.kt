package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.RecordBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class RecordAdapter(layoutResId: Int, data: MutableList<RecordBean>?) : BaseQuickAdapter<RecordBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: RecordBean) {
        helper.apply {
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoHour(item.date))
            setImageResource(R.id.iv_record,if (item.state==0) R.mipmap.icon_record_play else R.mipmap.icon_record_pause)
            setText(R.id.tv_save,if (item.isCommit) mContext.getString(R.string.homework_state_yes) else mContext.getString(R.string.commit))
            addOnClickListener(R.id.iv_record,R.id.iv_setting,R.id.tv_save)
        }
    }
}
