package com.bll.lnkstudy.ui.adapter


import android.widget.RelativeLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.TestPaper
import com.bll.lnkstudy.mvp.model.TestPaperType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperTypeAdapter(layoutResId: Int, data: List<TestPaperType>?) : BaseQuickAdapter<TestPaperType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaperType) {
        helper.setVisible(R.id.ll_rank,item.isPg)
        var llContent=helper.getView<RelativeLayout>(R.id.rl_content)
        llContent.setBackgroundResource(item.resId)
    }



}
