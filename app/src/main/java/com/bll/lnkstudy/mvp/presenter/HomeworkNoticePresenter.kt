package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class HomeworkNoticePresenter(view: IContractView.IHomeworkNoticeView, val screen: Int=0) : BasePresenter<IContractView.IHomeworkNoticeView>(view) {

    //获取批改通知
    fun getCorrectNotice(map:HashMap<String,Any>) {
        val type = RetrofitManager.service.getCorrectNotice(map)
        doRequest(type, object : Callback<HomeworkNoticeList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkNoticeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkNoticeList>) {
                if (tBaseResult.data!=null)
                    view.onCorrect(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 删除批改通知
     */
    fun deleteCorrectNotice() {
        val type = RetrofitManager.service.deleteCorrectNotice()
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }

    //获取作业通知
    fun getHomeworkNotice(map:HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkNotice(map)
        doRequest(type, object : Callback<HomeworkNoticeList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkNoticeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkNoticeList>) {
                if (tBaseResult.data!=null)
                    view.onHomeworkNotice(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 删除作业通知
     */
    fun deleteHomeworkNotice() {
        val type = RetrofitManager.service.deleteHomeworkNotice()
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }

}