package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.CalenderList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

/**
 * 台历相关
 */
class CalenderPresenter(view: IContractView.ICalenderView,val screen: Int =0) : BasePresenter<IContractView.ICalenderView>(view) {

    fun getList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getCalenderList(map)

        doRequest(app, object : Callback<CalenderList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<CalenderList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<CalenderList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, true)
    }

    fun buy(map: HashMap<String, Any> ) {

        val requestBody= RequestUtils.getBody(map)
        val download = RetrofitManager.service.buy(requestBody)

        doRequest(download, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }

        }, true)

    }

}