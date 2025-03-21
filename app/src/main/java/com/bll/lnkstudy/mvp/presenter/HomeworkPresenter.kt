package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.ParentTypeBean
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RequestUtils
import com.bll.lnkstudy.net.RetrofitManager

/**
 * 作业
 */
class HomeworkPresenter(view: IContractView.IHomeworkView,val screen:Int=0) : BasePresenter<IContractView.IHomeworkView>(view) {

    /**
     * 获取作业本列表
     */
    fun onCommitMessage() {
        val map=HashMap<String,Any>()
        map["size"]=10
        map["grade"]=MethodManager.getUser().grade
        val type = RetrofitManager.service.getHomeworkCommitDetails(map)
        doRequest(type, object : Callback<HomeworkCommitMessageList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkCommitMessageList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkCommitMessageList>) {
                if (tBaseResult.data!=null)
                    view.onCommitDetails(tBaseResult.data)
            }
        }, true)
    }

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkType(map)
        doRequest(type, object : Callback<List<HomeworkTypeBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<HomeworkTypeBean>>): Boolean {
                view.onTypeError()
                return false
            }
            override fun success(tBaseResult: BaseResult<List<HomeworkTypeBean>>) {
                if (tBaseResult.data!=null)
                    view.onTypeList(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取家长作业本列表
     */
    fun getTypeParentList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getParentsHomeworkType(map)
        doRequest(type, object : Callback<MutableList<ParentTypeBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<ParentTypeBean>>): Boolean {
                view.onTypeError()
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<ParentTypeBean>>) {
                if (tBaseResult.data!=null)
                    view.onTypeParentList(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取家长作业本消息
     */
    fun getParentMessage(map :HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.getParentMessage(body)
        doRequest(type, object : Callback<Map<String, ParentHomeworkMessageList>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String, ParentHomeworkMessageList>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String, ParentHomeworkMessageList>>) {
                if (tBaseResult.data!=null)
                    view.onParentMessageList(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取家长作业本批改下发
     */
    fun getParentReel(map :HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.getParentReel(body)
        doRequest(type, object : Callback<ParentHomeworkMessageList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ParentHomeworkMessageList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ParentHomeworkMessageList>) {
                if (tBaseResult.data!=null)
                    view.onParentReel(tBaseResult.data)
            }
        }, false)
    }


    /**
     * 家长下发作业卷下载成功
     */
    fun downloadParent(id:Int) {
        val body=RequestUtils.getBody(Pair.create("id",id))
        val type = RetrofitManager.service.commitParentLoad(body)
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

    /**
     * 获取老师下发作业消息
     */
    fun getMessageList(map: HashMap<String, Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.getHomeworkMessage(body)
        doRequest(type, object : Callback<Map<String, HomeworkMessageList>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String, HomeworkMessageList>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String, HomeworkMessageList>>) {
                if (tBaseResult.data!=null)
                    view.onMessageList(tBaseResult.data)
            }
        }, false)
    }

    fun getPaperList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getHomeworkPaperList(map)
        doRequest(type, object : Callback<HomeworkPaperList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkPaperList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkPaperList>) {
                if (tBaseResult.data!=null)
                    view.onPaperList(tBaseResult.data)
            }
        }, false)
    }


    fun downloadCompletePaper(id:Int) {
        val body = RequestUtils.getBody(
            Pair("studentTaskId", id)
        )
        val commit = RetrofitManager.service.onDownloadPaper(body)
        doRequest(commit, object : Callback<Any>(view, screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

}