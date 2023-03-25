package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.ReceivePaper
import com.bll.lnkstudy.mvp.model.homework.HomeworkType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

/**
 * 作业
 */
class HomeworkPresenter(view: IContractView.IHomeworkView) : BasePresenter<IContractView.IHomeworkView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkType(map)
        doRequest(type, object : Callback<HomeworkType>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkType>) {
                view.onTypeList(tBaseResult.data?.list)
            }
        }, false)
    }

    /**
     * 获取老师下发作业
     */
    fun getList() {
        val map=HashMap<String,Any>()
        map["size"] = 100
        map["type"] = 1
        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<ReceivePaper>(view) {
            override fun failed(tBaseResult: BaseResult<ReceivePaper>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ReceivePaper>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }

}