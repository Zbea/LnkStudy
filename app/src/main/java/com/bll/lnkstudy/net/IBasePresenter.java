package com.bll.lnkstudy.net;


public interface IBasePresenter<V extends IBaseView> {

    V getView();
}

/**
 * 还有类似下面的封装
 *
 * public interface IBasePresenter<V,E>
 */
