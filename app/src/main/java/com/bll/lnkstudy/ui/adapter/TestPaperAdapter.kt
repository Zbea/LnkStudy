package com.bll.lnkstudy.ui.adapter


import android.widget.ImageView
import android.widget.LinearLayout
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

        helper.addOnClickListener(R.id.ll_content)

        val ll_content=helper.getView<LinearLayout>(R.id.ll_content)
        for (url in item.images)
        {
            var imageView=getImageView(url)
            ll_content.addView(imageView)
        }

    }

    private fun getImageView(url:String):ImageView{
        val imageView=ImageView(mContext)
        val layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.marginStart=20
        imageView.layoutParams=layoutParams
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        Glide.with(mContext)
            .load(url).into(imageView)

        return imageView
    }


}
