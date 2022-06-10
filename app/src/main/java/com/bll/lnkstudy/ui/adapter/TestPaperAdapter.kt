package com.bll.lnkstudy.ui.adapter


import android.widget.RelativeLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.TestPaper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAdapter(layoutResId: Int, data: List<TestPaper>?) : BaseQuickAdapter<TestPaper, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaper) {
        helper.setVisible(R.id.ll_rank,item.isPg)
        var llContent=helper.getView<RelativeLayout>(R.id.rl_content)
        llContent.setBackgroundResource(item.resId)
    }



}
