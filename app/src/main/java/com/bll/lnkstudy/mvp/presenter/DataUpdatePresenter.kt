package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class DataUpdatePresenter(view: IContractView.IDataUpdateView):
    BasePresenter<IContractView.IDataUpdateView>(view) {

    fun onList(){
        val type = RetrofitManager.service.onListDataUpdate()
        doRequest(type, object : Callback<MutableList<DataUpdateBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<DataUpdateBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<DataUpdateBean>>) {
                if (!tBaseResult.data.isNullOrEmpty())
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun onList(map: HashMap<String, Any>){
        val type = RetrofitManager.service.onListDataUpdate(map)
        doRequest(type, object : Callback<MutableList<DataUpdateBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<DataUpdateBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<DataUpdateBean>>) {
                if (!tBaseResult.data.isNullOrEmpty())
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun onAddData(map: Map<String,Any>) {
        val body= RequestUtils.getBody(map)
        val type = RetrofitManager.service.onAddDataUpdate(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, false)
    }

    fun onDeleteData(map: Map<String,Any>) {
        val body= RequestUtils.getBody(map)
        val type = RetrofitManager.service.onDeleteDataUpdate(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, false)
    }

}