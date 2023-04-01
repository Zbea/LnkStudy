package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.mvp.model.homework.HomeworkReel
import com.bll.lnkstudy.mvp.model.homework.HomeworkType
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

/**
 * 作业
 */
class HomeworkPresenter(view: IContractView.IHomeworkView) : BasePresenter<IContractView.IHomeworkView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkType(map)
        doRequest(type, object : Callback<HomeworkType>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkType>) {
                view.onTypeList(tBaseResult.data?.list)
            }
        }, false)
    }

    /**
     * 获取老师下发作业
     */
    fun getList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getHomeworkMessage(map)
        doRequest(type, object : Callback<HomeworkMessage>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkMessage>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkMessage>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取老师下发作业卷
     */
    fun getReelList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getHomeworkReel(map)
        doRequest(type, object : Callback<HomeworkReel>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkReel>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkReel>) {
                if (tBaseResult.data!=null)
                    view.onListReel(tBaseResult.data)
            }
        }, false)
    }


    /**
     * 老师下发作业卷下载成功
     */
    fun commitDownload(id:Int) {
        val body=RequestUtils.getBody(Pair.create("studentTaskId",id))
        val type = RetrofitManager.service.commitHomeworkLoad(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

    fun commitHomework(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaper(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
    }

}