package com.bll.lnkstudy.mvp.presenter.cloud

import com.bll.lnkstudy.mvp.model.cloud.CloudHomeworkList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class CloudHomeworkPresenter(view: IContractView.ICloudHomeworkView) : BasePresenter<IContractView.ICloudHomeworkView>(view){
    /**
     * 获取作业本列表
     */
    fun getType(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getCloudHomeworkType(map)
        doRequest(type, object : Callback<CloudHomeworkList>(view) {
            override fun failed(tBaseResult: BaseResult<CloudHomeworkList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CloudHomeworkList>) {
                if (tBaseResult.data!=null)
                    view.onType(tBaseResult.data)
            }
        }, false)
    }
}