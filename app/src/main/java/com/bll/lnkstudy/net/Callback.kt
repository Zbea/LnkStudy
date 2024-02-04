package com.bll.lnkstudy.net


import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.R
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
            ExceptionHandle.ERROR.UNKONW_HOST_EXCEPTION -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.connect_error))
            }
            ExceptionHandle.ERROR.NETWORD_ERROR-> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.net_work_error))
            }
            ExceptionHandle.ERROR.SERVER_ADDRESS_ERROR -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.connect_server_timeout))
            }
            ExceptionHandle.ERROR.PARSE_ERROR -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.parse_data_error))
            }
            ExceptionHandle.ERROR.HTTP_ERROR -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.connect_error))
            }
            else -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.on_server_error))
            }
        }
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: BaseResult<T>): Boolean

    abstract fun success(tBaseResult: BaseResult<T>)
}