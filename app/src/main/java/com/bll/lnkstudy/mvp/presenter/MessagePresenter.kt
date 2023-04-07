package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.Message
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class MessagePresenter(view: IContractView.IMessageView): BasePresenter<IContractView.IMessageView>(view) {


    fun getList(map: HashMap<String,Any>,boolean: Boolean){
        val list= RetrofitManager.service.getMessages(map)
        doRequest(list, object : Callback<Message>(view) {
            override fun failed(tBaseResult: BaseResult<Message>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Message>) {
                if (tBaseResult?.data!=null)
                    view.onList(tBaseResult.data)
            }
        },boolean)
    }

}