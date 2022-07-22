package com.bll.lnkstudy.net


import com.bll.lnkstudy.utils.SToast
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

abstract class Callback<T> : Observer<BaseResult<T>> {

    private var IBaseView: IBaseView

    constructor(IBaseView: IBaseView) {
        this.IBaseView = IBaseView
    }


    override fun onSubscribe(@NonNull d: Disposable) {
        IBaseView?.addSubscription(d)
    }

    override fun onNext(@NonNull tBaseResult: BaseResult<T>) {
        if (!tBaseResult.error.isNullOrEmpty())
        {
            IBaseView?.fail(tBaseResult.error)
            return
        }
        if (tBaseResult.code == 0) {
            success(tBaseResult)
        } else {
            when {
                tBaseResult.code==1 -> {
                    IBaseView?.login()
                }
                else -> {
                    IBaseView?.fail(tBaseResult.msg)
                    failed(tBaseResult)
                }
            }
        }
    }

    override fun onComplete() {
        IBaseView?.hideLoading()
    }

    override fun onError(@NonNull e: Throwable) {
        e.printStackTrace()

        if ((e as HttpException).code()==401){
            IBaseView?.login()
        }

        SToast.showToast(ExceptionHandle.handleException(e))
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: BaseResult<T>): Boolean

    abstract fun success(tBaseResult: BaseResult<T>)
}