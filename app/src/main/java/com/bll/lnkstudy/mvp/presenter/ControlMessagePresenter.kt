package com.bll.lnkstudy.mvp.presenter

import android.util.Pair
import com.bll.lnkstudy.mvp.model.ControlMessage
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.*

/**
 * 老师发送控制指令、系统发送控制上传作业考卷指令
 */
class ControlMessagePresenter(view: IContractView.IControlMessageView) : BasePresenter<IContractView.IControlMessageView>(view) {

    fun getControlMessage() {

        val get = RetrofitManager.service.getControlMessage()

        doRequest(get, object : Callback<MutableList<ControlMessage>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<ControlMessage>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<ControlMessage>>) {
                if (tBaseResult.data!=null)
                    view.onControlMessage(tBaseResult.data)
            }

        }, false)

    }


    fun deleteControlMessage(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteControlMessage(body)

        doRequest(delete, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteMessage()
            }

        }, false)

    }


    fun getControlClearMessage() {

        val get = RetrofitManager.service.getControlMessage()

        doRequest(get, object : Callback<MutableList<ControlMessage>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<ControlMessage>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<ControlMessage>>) {
                if (tBaseResult.data!=null)
                    view.onControlClear(tBaseResult.data)
            }

        }, false)

    }


    fun deleteClearMessage(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteControlMessage(body)

        doRequest(delete, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteClear()
            }

        }, false)

    }

    fun editGrade(grade: Int) {

        val body = RequestUtils.getBody(
            Pair.create("grade", grade)
        )

        val editName = RetrofitManager.service.editGrade(body)

        doRequest(editName, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditGradeSuccess()
            }

        }, true)

    }


}