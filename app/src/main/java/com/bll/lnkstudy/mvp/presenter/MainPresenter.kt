package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ExamItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainPresenter(view: IContractView.IMainView,val screen: Int=0) : BasePresenter<IContractView.IMainView>(view) {

    fun getExam(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getExams(map)
        doRequest(type, object : Callback<ExamItem>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamItem>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamItem>) {
                if (tBaseResult.data!=null)
                    view.onExam(tBaseResult.data)
            }
        }, false)
    }

    fun getHomeworkNotice() {
        val map=HashMap<String,Any>()
        map["size"]=7
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

    //班群列表
    fun getClassGroupList() {
        val list= RetrofitManager.service.groupList()
        doRequest(list, object : Callback<List<ClassGroup>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroup>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroup>>) {
                if (tBaseResult.data!=null)
                    view.onClassGroupList(tBaseResult.data)
            }
        }, false)
    }

    //获取更新信息
    fun getAppUpdate() {
        val list= RetrofitManager.service.onAppUpdate()
        doRequest(list, object : Callback<AppUpdateBean>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AppUpdateBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AppUpdateBean>) {
                if (tBaseResult.data!=null)
                    view.onAppUpdate(tBaseResult.data)
            }
        }, false)
    }

    fun active() {
        val type = RetrofitManager.service.active()
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }


}