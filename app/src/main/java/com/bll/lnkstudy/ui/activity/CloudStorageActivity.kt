package com.bll.lnkstudy.ui.activity

import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity

/**
 * 书架收藏
 */
class CloudStorageActivity: BaseAppCompatActivity() {

    override fun layoutId(): Int {
        return R.layout.ac_cloud_storage
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle(R.string.cloud_storage_str)
    }


}