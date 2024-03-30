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

    fun getList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPapersList(map)
        doRequest(type, object : Callback<PaperList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<PaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<PaperList>) {
                view.onList(tBaseResult.data)
            }
        }, false)
    }


    fun deletePaper(id:Int){
        val body= RequestUtils.getBody(
            Pair("studentTaskId",id)
        )
        val commit = RetrofitManager.service.deletePaper(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, false)
    }

    fun getExamList() {
        val type = RetrofitManager.service.getExamCorrectList()
        doRequest(type, object : Callback<Map<Int,MutableList<ExamCorrectBean>>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<Int,MutableList<ExamCorrectBean>>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<Int,MutableList<ExamCorrectBean>>>) {
                if (tBaseResult.data!=null)
                    view.onExamList(tBaseResult.data)
            }
        }, false)
    }

    fun deleteExam(id:Int){
        val body= RequestUtils.getBody(
            Pair("id",id)
        )
        val commit = RetrofitManager.service.onDeleteExamCorrect(body)
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