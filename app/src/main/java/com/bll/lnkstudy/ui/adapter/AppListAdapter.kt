package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.utils.BitmapUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(private val type:Int, layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.run {
            item.run{
                setText(R.id.tv_name,appName)
                setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(imageByte))
                if (type==0){
                    setGone(R.id.cb_check,!isBase)
                }
                else
                {
                    setGone(R.id.cb_check,false)
                }
                setChecked(R.id.cb_check,isCheck)
            }
            addOnClickListener(R.id.iv_image,R.id.cb_check)
        }
    }



}
