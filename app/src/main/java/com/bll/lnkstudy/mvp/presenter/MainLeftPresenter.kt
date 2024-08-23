package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainLeftPresenter(view: IContractView.IMainLeftView, val screen: Int=0) : BasePresenter<IContractView.IMainLeftView>(view) {

    //获取批改通知
    fun getCorrectNotice() {
        val map=HashMap<String,Any>()
        map["size"]=7
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

    //获取学生科目列表
    fun getCourseItems() {
        val list= RetrofitManager.service.getCourseItems()
        doRequest(list, object : Callback<List<String>>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<List<String>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<String>>) {
                if (tBaseResult.data!=null)
                    view.onCourseItems(tBaseResult.data)
            }
        }, false)
    }

    //获取更新信息
    fun getAppUpdate() {
        val list= RetrofitManager.service.onAppUpdate()
        doRequest(list, object : Callback<AppUpdateBean>(view,screen,false) {
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
        doRequest(type, object : Callback<Any>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)
    }

    fun getTeachingType() {
        val list = RetrofitManager.service.getTeachType()
        doRequest(list, object : Callback<TeachingVideoType>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<TeachingVideoType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeachingVideoType>) {
                view.onType(tBaseResult.data)
            }
        }, false)
    }

    fun getParentPermission() {
        val type = RetrofitManager.service.getPermissionParentAllow()
        doRequest(type, object : Callback<PermissionParentBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<PermissionParentBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PermissionParentBean>) {
                if (tBaseResult.data!=null)
                    view.onParentPermission(tBaseResult.data)
            }
        }, false)
    }

    fun getSchoolPermission() {
        val type = RetrofitManager.service.getPermissionSchoolAllow()
        doRequest(type, object : Callback<PermissionSchoolBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<PermissionSchoolBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PermissionSchoolBean>) {
                if (tBaseResult.data!=null)
                    view.onSchoolPermission(tBaseResult.data)
            }
        }, false)
    }

}