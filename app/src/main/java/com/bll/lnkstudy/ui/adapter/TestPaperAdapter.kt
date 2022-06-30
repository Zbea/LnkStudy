package com.bll.lnkstudy.ui.adapter


import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.TestPaper
import com.bll.lnkstudy.utils.StringUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAdapter(layoutResId: Int, data: List<TestPaper>?) : BaseQuickAdapter<TestPaper, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaper) {
        helper.setText(R.id.tv_title,item.name)
        helper.setVisible(R.id.ll_score,item.isPg)
        helper.setVisible(R.id.btn_ok,!item.isPg)
        helper.setText(R.id.tv_rank,item.rank.toString())
        helper.setText(R.id.tv_score,item.score.toString())
        helper.setText(R.id.tv_date, StringUtils.longToStringDataNoHour(item.createDate))
        val image1=helper.getView<ImageView>(R.id.iv_content1)
        Glide.with(mContext).load(item.image).into(image1)
        val image2=helper.getView<ImageView>(R.id.iv_content2)
        Glide.with(mContext).load(item.image).into(image2)

        helper.addOnClickListener(R.id.iv_content1)

    }



}
