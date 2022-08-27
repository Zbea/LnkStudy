package com.bll.lnkstudy.ui.adapter

import android.widget.CheckBox
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.setText(R.id.tv_name,item.appName)
        helper.setImageDrawable(R.id.iv_image,item.image)

        helper.setGone(R.id.cb_check,!item.isBase)

        helper.setChecked(R.id.cb_check,item.isCheck)
        helper.getView<CheckBox>(R.id.cb_check).setOnCheckedChangeListener { compoundButton, b ->
            item.isCheck=b
        }
        helper.addOnClickListener(R.id.iv_image)

    }



}
