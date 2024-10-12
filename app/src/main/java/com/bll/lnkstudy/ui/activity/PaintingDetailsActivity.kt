package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_painting_details.iv_image

class PaintingDetailsActivity:BaseAppCompatActivity() {
    private lateinit var paintingBean:PaintingBean

    override fun layoutId(): Int {
        return R.layout.ac_painting_details
    }

    override fun initData() {
        val flags=intent.flags
        paintingBean=PaintingBeanDaoManager.getInstance().queryBean(flags)
        pageSize=1
        pageCount=paintingBean.paths.size
    }

    override fun initView() {
        setPageTitle(paintingBean.title)
        setPageNumber(pageCount)
        fetchData()
    }

    override fun fetchData() {
        GlideUtils.setImageUrl(this,paintingBean.paths[pageIndex],iv_image)
    }

}