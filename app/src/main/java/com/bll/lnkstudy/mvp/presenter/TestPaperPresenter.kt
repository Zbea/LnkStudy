package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager

class TestPaperPresenter(view: IContractView.IPaperView,val screen:Int=0): BasePresenter<IContractView.IPaperView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<List<PaperTypeBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<PaperTypeBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<PaperTypeBean>>) {
                if (tBaseResult.data!=null)
                    view.onTypeList(tBaseResult.data)
            }
        }, false)
    }

    fun getPaperList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPaperList(map)
        doRequest(type, object : Callback<HomeworkPaperList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkPaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkPaperList>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }


    fun downloadCompletePaper(id:Int){
        val body= RequestUtils.getBody(
            Pair("studentTaskId",id)
        )
        val commit = RetrofitManager.service.onDownloadPaper(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

    fun getExamList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getExamCorrectList(map)
        doRequest(type, object : Callback<MutableList<ExamCorrectBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<ExamCorrectBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<ExamCorrectBean>>) {
                if (tBaseResult.data!=null)
                    view.onExamList(tBaseResult.data)
            }
        }, false)
    }

    fun downloadCompleteExam(id:Int){
        val body= RequestUtils.getBody(
            Pair("id",id)
        )
        val commit = RetrofitManager.service.onDownloadCompleteExamCorrect(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

}