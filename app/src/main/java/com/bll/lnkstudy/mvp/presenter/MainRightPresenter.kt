package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager


class MainRightPresenter(view: IContractView.IMainRightView, val screen: Int=0) : BasePresenter<IContractView.IMainRightView>(view) {

    //获取老师下发考试卷
    fun getExam() {
        val type = RetrofitManager.service.getExams()
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

    /**
     * 获取老师课程表
     */
    fun getTeacherCourse() {
        val list = RetrofitManager.service.getTeacherCourse()
        doRequest(list, object : Callback<String>(view,screen) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                view.onCourse(tBaseResult.data)
            }
        }, false)
    }

    //班群列表
    fun getClassGroupList(boolean: Boolean) {
        val list= RetrofitManager.service.groupList()
        doRequest(list, object : Callback<List<ClassGroup>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroup>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroup>>) {
                if (tBaseResult.data!=null)
                    view.onClassGroupList(tBaseResult.data)
            }
        }, boolean)
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
}