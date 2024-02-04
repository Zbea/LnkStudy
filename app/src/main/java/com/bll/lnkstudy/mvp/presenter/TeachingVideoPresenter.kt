package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class TeachingVideoPresenter(view: IContractView.ITeachingVideoView,var screen:Int=0) : BasePresenter<IContractView.ITeachingVideoView>(view) {

    fun getCourseList(map: HashMap<String,Any>) {
        val list = RetrofitManager.service.getTeachCourseList(map)
        doRequest(list, object : Callback<TeachingVideoList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TeachingVideoList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeachingVideoList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun getList(map: HashMap<String,Any>) {
        val list = RetrofitManager.service.getTeachList(map)
        doRequest(list, object : Callback<TeachingVideoList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TeachingVideoList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeachingVideoList>) {
                view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun getType() {
        val list = RetrofitManager.service.getTeachType()
        doRequest(list, object : Callback<TeachingVideoType>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TeachingVideoType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeachingVideoType>) {
                if (tBaseResult!=null)
                    view.onType(tBaseResult.data)
            }
        }, false)
    }
}