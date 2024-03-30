package com.bll.lnkstudy.net


import com.bll.lnkstudy.utils.SToast
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

abstract class Callback<T> : Observer<BaseResult<T>> {

    private var IBaseView: IBaseView
    private var screen=0
    private var isComplete=true

    constructor(IBaseView: IBaseView) {
        this.IBaseView = IBaseView
    }

    constructor(IBaseView: IBaseView,screen:Int) {
        this.IBaseView = IBaseView
        this.screen=screen
    }

    constructor(IBaseView: IBaseView,screen:Int,isComplete: Boolean) {
        this.IBaseView = IBaseView
        this.screen=screen
        this.isComplete=isComplete
    }

    override fun onSubscribe(@NonNull d: Disposable) {
        IBaseView.addSubscription(d)
    }

    override fun onNext(@NonNull tBaseResult: BaseResult<T>) {
        if (!tBaseResult.error.isNullOrEmpty()) {
            IBaseView.fail(tBaseResult.error)
            return
        }
        if (tBaseResult.code == 0) {
            success(tBaseResult)
        } else {
            when (tBaseResult.code) {
                -10 -> {
                    IBaseView.login()
                }
                else -> {
                    IBaseView.fail(tBaseResult.msg)
                    failed(tBaseResult)
                }
            }
        }
    }

    override fun onComplete() {
        if (isComplete)
            IBaseView.hideLoading()
    }

    override fun onError(@NonNull e: Throwable) {
        e.printStackTrace()

        when (ExceptionHandle.handleException(e).code) {
            ExceptionHandle.ERROR.NETWORD_ERROR-> {
                SToast.showText(screen,"网络连接失败")
            }
            ExceptionHandle.ERROR.SERVER_TIMEOUT_ERROR -> {
                SToast.showText(screen,"请求超时")
            }
            ExceptionHandle.ERROR.PARSE_ERROR -> {
                SToast.showText(screen,"数据解析错误")
            }
            ExceptionHandle.ERROR.HTTP_ERROR -> {
                SToast.showText(screen,"服务器连接失败")
            }
            else -> {
                SToast.showText(screen,"服务器开小差，请重试")
            }
        }
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: BaseResult<T>): Boolean

    abstract fun success(tBaseResult: BaseResult<T>)
}