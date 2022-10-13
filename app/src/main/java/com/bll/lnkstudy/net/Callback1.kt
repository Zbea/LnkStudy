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
        val code: Int = ExceptionHandle.handleException(e).code
        if (code == ExceptionHandle.ERROR.UNKONW_HOST_EXCEPTION) {
            SToast.showText(2,MyApplication.mContext.getString(R.string.net_work_error))
        } else if (code == ExceptionHandle.ERROR.NETWORD_ERROR || code == ExceptionHandle.ERROR.SERVER_ADDRESS_ERROR) {
            SToast.showText(2,MyApplication.mContext.getString(R.string.connect_server_timeout))
        } else if (code == ExceptionHandle.ERROR.PARSE_ERROR) {
            SToast.showText(2,MyApplication.mContext.getString(R.string.parse_data_error))
        } else if (code == ExceptionHandle.ERROR.HTTP_ERROR) {
            SToast.showText(2,MyApplication.mContext.getString(R.string.connect_error))
        } else {
            SToast.showText(2,MyApplication.mContext.getString(R.string.on_server_error))
        }
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: T): Boolean

    abstract fun success(iss: T)
}