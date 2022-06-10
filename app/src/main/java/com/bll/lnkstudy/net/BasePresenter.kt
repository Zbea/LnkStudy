package com.bll.lnkstudy.net

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class BasePresenter<T : IBaseView>(private val view: T) : IBasePresenter<IBaseView> {

    protected val context: Context?
        get() = when (view) {
            is Activity -> view
            is Fragment -> (view as Fragment).context
            is android.app.Fragment -> (view as android.app.Fragment).activity
            else -> null
        }

    override fun getView(): T {
        return view
    }

    protected fun <V> doRequest(observable: Observable<BaseResult<V>>?, observer: Observer<BaseResult<V>>) {
        if (observable == null) {
            return
        }
        doRequest(observable, observer, true)
    }

     fun <V> doRequest(observable: Observable<BaseResult<V>>?,
                       observer: Observer<BaseResult<V>>,
                       showLoading: Boolean) {
        if (observable == null) {
            return
        }
        if (showLoading) this.view.showLoading()

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    protected fun <V> doRequest1(observable: Observable<V>?,
                                observer: Observer<V>,
                                showLoading: Boolean) {
        if (observable == null) {
            return
        }
        if (showLoading) this.view.showLoading()
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }


}



