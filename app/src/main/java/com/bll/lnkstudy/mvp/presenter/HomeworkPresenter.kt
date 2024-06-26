package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

/**
 * 作业
 */
class HomeworkPresenter(view: IContractView.IHomeworkView,val screen:Int=0) : BasePresenter<IContractView.IHomeworkView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkType(map)
        doRequest(type, object : Callback<HomeworkType>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkType>) {
                if (tBaseResult.data!=null)
                {
                    if (!tBaseResult.data?.list.isNullOrEmpty()){
                        view.onTypeList(tBaseResult.data?.list)
                    }
                }
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
        doRequest(type, object : Callback<Map<String,ParentHomeworkMessage>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String,ParentHomeworkMessage>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String,ParentHomeworkMessage>>) {
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
        doRequest(type, object : Callback<Map<String,MutableList<ParentHomeworkBean>>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String,MutableList<ParentHomeworkBean>>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String,MutableList<ParentHomeworkBean>>>) {
                if (tBaseResult.data!=null)
                    view.onParentReel(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取老师下发作业消息
     */
    fun getList(map: HashMap<String, Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.getHomeworkMessage(body)
        doRequest(type, object : Callback<Map<String, HomeworkMessage>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String,HomeworkMessage>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String, HomeworkMessage>>) {
                if (tBaseResult.data!=null)
                    view.onMessageList(tBaseResult.data)
            }
        }, false)
    }

    /**
     * 获取老师下发作业卷
     */
    fun getReelList(map: HashMap<String, Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.getHomeworkReel(body)
        doRequest(type, object : Callback<Map<String, HomeworkPaperList>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Map<String, HomeworkPaperList>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Map<String, HomeworkPaperList>>) {
                if (tBaseResult.data!=null)
                    view.onListReel(tBaseResult.data)
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
     * 老师下发作业卷下载成功
     */
    fun commitDownload(id:Int) {
        val body=RequestUtils.getBody(Pair.create("studentTaskId",id))
        val type = RetrofitManager.service.commitHomeworkLoad(body)
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDownloadSuccess()
            }
        }, false)
    }

}