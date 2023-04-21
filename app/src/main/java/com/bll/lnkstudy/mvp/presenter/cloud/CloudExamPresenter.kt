package com.bll.lnkstudy.mvp.presenter.cloud

import com.bll.lnkstudy.mvp.model.cloud.CloudExamList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class CloudExamPresenter(view: IContractView.ICloudExamView) : BasePresenter<IContractView.ICloudExamView>(view){
    /**
     * 获取作业本列表
     */
    fun getType(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getCloudExamType(map)
        doRequest(type, object : Callback<CloudExamList>(view) {
            override fun failed(tBaseResult: BaseResult<CloudExamList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CloudExamList>) {
                if (tBaseResult.data!=null)
                    view.onType(tBaseResult.data)
            }
        }, false)
    }
}