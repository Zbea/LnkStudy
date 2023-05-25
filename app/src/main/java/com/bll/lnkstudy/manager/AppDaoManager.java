package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class AppDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static AppDaoManager mDbController;

    private AppBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= AppBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public AppDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getAppBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static AppDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (AppDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new AppDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(AppBean bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<AppBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    /**
     * @return
     */
    public List<AppBean> queryAll() {
        List<AppBean> queryList = dao.queryBuilder().where(whereUser).build().list();
        return queryList;
    }

    public void deleteBean(AppBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
