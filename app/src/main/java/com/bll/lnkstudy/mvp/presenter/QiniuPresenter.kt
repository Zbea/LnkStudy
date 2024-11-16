package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class QiniuPresenter(view: IContractView.IQiniuView,val screen:Int=1): BasePresenter<IContractView.IQiniuView>(view) {

    fun getToken(boolean: Boolean=false){
        val token = RetrofitManager.service.getQiniuToken()
        doRequest(token, object : Callback<String>(view,screen,false,false) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                if (tBaseResult.data!=null)
                    view.onToken(tBaseResult.data)
            }
        }, boolean)
    }


}