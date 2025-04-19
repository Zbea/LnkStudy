package com.bll.lnkstudy.base

import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.presenter.CloudPresenter
import com.bll.lnkstudy.mvp.view.IContractView


abstract class
BaseCloudFragment : BaseFragment(), IContractView.ICloudView  {

    var mCloudPresenter= CloudPresenter(this,getScreenPosition())
    var types= mutableListOf<String>()

    override fun onList(cloudListBean: CloudList) {
        onCloudList(cloudListBean)
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

    /**
     * 删除后刷新页面
     */
    fun onRefreshList(list:List<Any>){
        if (pageIndex==1&&pageCount==1){
            if (list.isEmpty()){
                setPageNumber(0)
            }
        }
        else if (pageCount>1&&pageCount>pageIndex){
            fetchData()
        }
        else if (pageIndex==pageCount&&pageIndex>1){
            if (list.isEmpty()){
                pageIndex-=1
                fetchData()
            }
        }
    }
}
