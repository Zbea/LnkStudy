package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Notebook
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NotebookAdapter(layoutResId: Int, data: List<Notebook>?) : BaseQuickAdapter<Notebook, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Notebook) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, if (item.type==0)item.dateStr else DateUtils.longToStringDataNoYear(item.createDate))
        helper.setGone(R.id.iv_encrypt,item.type==0)
        helper.setImageResource(R.id.iv_encrypt,if (item.isEncrypt) R.mipmap.icon_encrypt_check else R.mipmap.icon_encrypt)
        helper.addOnClickListener(R.id.iv_encrypt)
        helper.addOnClickListener(R.id.iv_more)
    }

}
