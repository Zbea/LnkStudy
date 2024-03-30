package com.bll.lnkstudy.base

import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.presenter.CloudPresenter
import com.bll.lnkstudy.mvp.view.IContractView


abstract class BaseCloudFragment : BaseFragment(), IContractView.ICloudView  {

    var mCloudPresenter= CloudPresenter(this,getScreenPosition())
    var types= mutableListOf<String>()

    override fun onList(item: CloudList) {
        onCloudList(item)
    }
    override fun onType(types: MutableList<String>) {
        onCloudType(types)
    }
    override fun onDelete() {
        onCloudDelete()
    }

    /**
     * 更改年级
     */
    open fun changeGrade(grade: Int){
        this.grade=grade
        pageIndex=1
        fetchData()
    }

    override fun initChangeScreenData() {
        super.initChangeScreenData()
        mCloudPresenter= CloudPresenter(this,getScreenPosition())
    }

    /**
     * 获取云数据
     */
    open fun onCloudList(item: CloudList){

    }
    /**
     * 获取云分类
     */
    open fun onCloudType(types: MutableList<String>){

    }
    /**
     * 删除云数据
     */
    open fun onCloudDelete(){

    }
}
