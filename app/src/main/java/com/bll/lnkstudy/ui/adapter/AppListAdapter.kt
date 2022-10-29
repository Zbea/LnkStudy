package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(private val type:Int, layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.setText(R.id.tv_name,item.appName)
        helper.setImageDrawable(R.id.iv_image,item.image)
        if (type==0){
            helper.setGone(R.id.cb_check,!item.isBase)
        }
        else
        {
            helper.setGone(R.id.cb_check,false)
        }

        helper.setChecked(R.id.cb_check,item.isCheck)

        helper.addOnClickListener(R.id.iv_image,R.id.cb_check)

    }



}
