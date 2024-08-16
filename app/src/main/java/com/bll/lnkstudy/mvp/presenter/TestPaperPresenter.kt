package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.model.paper.PaperType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

class TestPaperPresenter(view: IContractView.IPaperView,val screen:Int=0): BasePresenter<IContractView.IPaperView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<PaperType>(view,screen) {
            override fun failed(tBaseResult: BaseResult<PaperType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperType>) {
                if (tBaseResult.data!=null)
                    view.onTypeList(tBaseResult.data?.list)
            }
        }, false)
    }

    fun getPaperCorrectList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPaperCorrectList(map)
        doRequest(type, object : Callback<PaperList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<PaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperList>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }


    fun downloadCompletePaper(id:Int){
        val body= RequestUtils.getBody(
            Pair("studentTaskId",id)
        )
        val commit = RetrofitManager.service.onDownloadCompletePaper(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
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
                view.onDeleteSuccess()
            }
        }, false)
    }

}