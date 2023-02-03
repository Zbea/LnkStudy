package com.bll.lnkstudy.mvp.view;

import com.bll.lnkstudy.mvp.model.AccountXDList;
import com.bll.lnkstudy.mvp.model.AccountOrder;
import com.bll.lnkstudy.mvp.model.AppList;
import com.bll.lnkstudy.mvp.model.BookStore;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ClassGroupUser;
import com.bll.lnkstudy.mvp.model.BookStoreType;
import com.bll.lnkstudy.mvp.model.PaintingList;
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
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void onXdList(AccountXDList list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城教材
    interface IBookStoreView extends IBaseView {
        void onBook(BookStore bookStore);
        void onType(BookStoreType bookStoreType);
        void buyBookSuccess();
    }

    //应用
    interface IAPPView extends IBaseView {
        void onAppList(AppList appBean);
        void buySuccess();
    }

    //书画以及壁纸
    interface IPaintingView extends IBaseView {
        void onList(PaintingList bean);
        void buySuccess();
    }

    //班群管理
    interface IClassGroupView extends IBaseView {
        void onInsert();
        void onClassGroupList(List<ClassGroup> classGroups);
        void onQuit();
        void onUser(List<ClassGroupUser> lists);
    }
}
