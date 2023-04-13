package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.model.paper.PaperType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class TestPaperPresenter(view: IContractView.IPaperView): BasePresenter<IContractView.IPaperView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<PaperType>(view) {
            override fun failed(tBaseResult: BaseResult<PaperType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperType>) {
                if (tBaseResult.data!=null)
                    view.onTypeList(tBaseResult.data?.list)
            }
        }, false)
    }

    fun getList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<PaperList>(view) {
            override fun failed(tBaseResult: BaseResult<PaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperList>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }


    fun commitPaper(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaper(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

    fun deletePaper(id:Int){
        val body= RequestUtils.getBody(
            Pair("studentTaskId",id)
        )
        val commit = RetrofitManager.service.deletePaper(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, false)
    }

}