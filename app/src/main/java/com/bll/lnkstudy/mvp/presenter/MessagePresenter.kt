package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class MessagePresenter(view: IContractView.IMessageView,var screen:Int=0): BasePresenter<IContractView.IMessageView>(view) {


    fun getList(map: HashMap<String,Any>,boolean: Boolean){
        val list= RetrofitManager.service.getMessages(map)
        doRequest(list, object : Callback<MessageList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MessageList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MessageList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        },boolean)
    }

    fun commitMessage(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val list= RetrofitManager.service.commitMessage(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                    view.onCommitSuccess()
            }
        },true)
    }

}