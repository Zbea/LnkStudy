package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.ControlMessage
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


class ControlMessagePresenter(view: IContractView.IControlMessageView) : BasePresenter<IContractView.IControlMessageView>(view) {

    fun getControlMessage() {

        val get = RetrofitManager.service.getControlMessage()

        doRequest(get, object : Callback<MutableList<ControlMessage>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<ControlMessage>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<ControlMessage>>) {
                if (tBaseResult.data!=null)
                    view.onControl(tBaseResult.data)
            }

        }, false)

    }


    fun deleteControlMessage(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteControlMessage(body)

        doRequest(delete, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDelete()
            }

        }, false)

    }


}