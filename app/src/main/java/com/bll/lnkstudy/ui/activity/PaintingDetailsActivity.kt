package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_painting_details.*

class PaintingDetailsActivity:BaseAppCompatActivity() {
    private lateinit var paintingBean:PaintingBean

    override fun layoutId(): Int {
        return R.layout.ac_painting_details
    }

    override fun initData() {
        val flags=intent.flags
        paintingBean=PaintingBeanDaoManager.getInstance().queryBean(flags)
    }

    override fun initView() {
        setPageTitle(paintingBean.title)
        GlideUtils.setImageUrl(this,paintingBean.bodyUrl,iv_image)
    }

}