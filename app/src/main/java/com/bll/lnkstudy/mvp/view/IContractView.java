package com.bll.lnkstudy.mvp.view;

import com.bll.lnkstudy.mvp.model.AccountList;
import com.bll.lnkstudy.mvp.model.AccountOrder;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.BookEvent;
import com.bll.lnkstudy.mvp.model.BookStore;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.net.IBaseView;

import java.util.List;

public interface IContractView {

    //登录
    interface ILoginView extends IBaseView {
        void getLogin(User user);
        void getAccount(User user);
    }

    //注册 找回密码
    interface IRegisterOrFindPsdView extends IBaseView {
        void onSms();
        void onRegister();
        void onFindPsd();
        void onEditPsd();
    }

    //账户页面回调
    interface IAccountInfoView extends IBaseView {
        void onLogout();
        void onEditNameSuccess();
        void getVipList(AccountList list);
        void onVipOrder(AccountOrder order);
        void onInsert();
        void onClassGroupList(List<ClassGroup> list);
        void onQuit();
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void getXdList(AccountList list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城
    interface IBookStoreView extends IBaseView {
        void onBookStore(BookStore bookStore);//商城列表
        void onBuyBook(BookEvent bookEvent);//购买书籍回调
        void onDownBook(BookEvent bookEvent);//下载书籍回调
    }

    //应用
    interface IAPPView extends IBaseView {
        void onAppList(AppBean appBean);
        void onDownBook(AppBean appBean);
    }

}
