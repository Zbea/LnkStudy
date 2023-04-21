package com.bll.lnkstudy.mvp.presenter.cloud

import com.bll.lnkstudy.mvp.model.cloud.CloudPaintingList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class CloudPaintingPresenter(view: IContractView.ICloudPaintingView) : BasePresenter<IContractView.ICloudPaintingView>(view){

    /**
     * 获取书画列表
     */
    fun getType(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getCloudPaintingList(map)
        doRequest(type, object : Callback<CloudPaintingList>(view) {
            override fun failed(tBaseResult: BaseResult<CloudPaintingList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CloudPaintingList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }
}