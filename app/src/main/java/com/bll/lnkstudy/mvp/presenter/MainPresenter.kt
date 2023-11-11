package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainPresenter(view: IContractView.IMainView) : BasePresenter<IContractView.IMainView>(view) {

    fun getExam(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<PaperList>(view) {
            override fun failed(tBaseResult: BaseResult<PaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperList>) {
                if (tBaseResult.data!=null)
                    view.onExam(tBaseResult.data)
            }
        }, false)
    }

    fun getHomeworkNotice() {
        val map=HashMap<String,Any>()
        map["size"]=7
        val type = RetrofitManager.service.getHomeworkNotice(map)
        doRequest(type, object : Callback<HomeworkNoticeList>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkNoticeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkNoticeList>) {
                if (tBaseResult.data!=null)
                    view.onHomeworkNotice(tBaseResult.data)
            }
        }, false)
    }

    fun deleteHomeworkNotice() {
        val type = RetrofitManager.service.deleteHomeworkNotice()
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }

    //班群列表
    fun getClassGroupList() {
        val list= RetrofitManager.service.groupList()
        doRequest(list, object : Callback<List<ClassGroup>>(view) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroup>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroup>>) {
                if (tBaseResult.data!=null)
                    view.onClassGroupList(tBaseResult.data)
            }
        }, false)
    }



}