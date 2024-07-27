package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class FileUploadPresenter(view: IContractView.IFileUploadView,val screen:Int=0):
    BasePresenter<IContractView.IFileUploadView>(view) {

    fun getToken(){
        val token = RetrofitManager.service.getQiniuToken()
        doRequest(token, object : Callback<String>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                if (tBaseResult.data!=null)
                    view.onToken(tBaseResult.data)
            }
        }, false)
    }

    fun commit(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaper(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

    fun commitExam(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitExam(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

    fun commitParent(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitParent(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

}