package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.painting.PaintingList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*


/**
 * 书画 壁纸
 */
class DownloadPaintingPresenter(view: IContractView.IPaintingView) : BasePresenter<IContractView.IPaintingView>(view) {

    fun getList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getPaintings(map)

        doRequest(app, object : Callback<PaintingList>(view) {
            override fun failed(tBaseResult: BaseResult<PaintingList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<PaintingList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, true)
    }


    fun buy(map: HashMap<String, Any> ) {

        val requestBody=RequestUtils.getBody(map)
        val download = RetrofitManager.service.buyPainting(requestBody)

        doRequest(download, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }

        }, true)

    }

}