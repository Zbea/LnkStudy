package com.bll.lnkstudy.ui.adapter

import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.BookStoreType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookCaseTypeAdapter(layoutResId: Int, data: List<BookStoreType>?) : BaseQuickAdapter<BookStoreType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookStoreType) {
        var tv_name=helper.getView<TextView>(R.id.tv_name)
        tv_name.text=item.title
        if (item.isCheck)
            tv_name.setBackgroundResource(R.drawable.bg_line_bottom_black)
        else
            tv_name.setBackgroundResource(R.color.color_transparent)

    }

}
