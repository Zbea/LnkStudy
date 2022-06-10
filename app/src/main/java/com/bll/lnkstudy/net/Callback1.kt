package com.bll.lnkstudy.net


import com.bll.lnkstudy.utils.SToast
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

//下载stream
abstract class Callback1<T> : Observer<T> {

    private var IBaseView: IBaseView

    constructor(IBaseView: IBaseView) {
        this.IBaseView = IBaseView
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
        IBaseView?.hideLoading()
    }

    override fun onError(@NonNull e: Throwable) {
        e.printStackTrace()
        SToast.showToast(ExceptionHandle.handleException(e))
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: T): Boolean

    abstract fun success(iss: T)
}