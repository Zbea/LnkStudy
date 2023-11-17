package com.bll.lnkstudy.net


import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.SToast
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

//下载stream
abstract class Callback1<T> : Observer<T> {

    private var IBaseView: IBaseView
    private var screen=0

    constructor(IBaseView: IBaseView) {
        this.IBaseView = IBaseView
    }

    constructor(IBaseView: IBaseView,screen:Int) {
        this.IBaseView = IBaseView
        this.screen=screen
    }


    override fun onSubscribe(@NonNull d: Disposable) {
        IBaseView?.addSubscription(d)
    }

    override fun onNext(@NonNull iss: T) {
        if (iss!=null)
        {
            success(iss)
        }
        else{
            failed(iss)
        }
    }

    override fun onComplete() {
        IBaseView.hideLoading()
    }

    override fun onError(@NonNull e: Throwable) {
        e.printStackTrace()
        when (ExceptionHandle.handleException(e).code) {
            ExceptionHandle.ERROR.UNKONW_HOST_EXCEPTION -> {
                SToast.showText(screen,MyApplication.mContext.getString(R.string.connect_error))
            }
            ExceptionHandle.ERROR.NETWORD_ERROR -> {
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

    abstract fun failed(tBaseResult: T): Boolean

    abstract fun success(iss: T)
}