package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NotebookAdapter(layoutResId: Int, data: List<NotebookBean>?) : BaseQuickAdapter<NotebookBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: NotebookBean) {
        helper.apply {
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date, if (item.type==0)item.dateStr else DateUtils.longToStringDataNoYear(item.createDate))
            setGone(R.id.iv_encrypt,item.type==0)
            setImageResource(R.id.iv_encrypt,if (item.isEncrypt) R.mipmap.icon_encrypt_check else R.mipmap.icon_encrypt)
            addOnClickListener(R.id.iv_encrypt)
            addOnClickListener(R.id.iv_more)
        }
    }

}
