package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class AppDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static AppDaoManager mDbController;

    private final AppBeanDao dao;

    private static WhereCondition whereUser;

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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= AppBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(AppBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<AppBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public AppBean queryAllByPackageName(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition where2= AppBeanDao.Properties.IsTool.eq(true);
        return dao.queryBuilder().where(whereUser,whereCondition,where2).build().unique();
    }

    public List<AppBean> queryToolAll() {
        WhereCondition whereCondition=AppBeanDao.Properties.IsTool.eq(true);
        return dao.queryBuilder().orderAsc(AppBeanDao.Properties.Time).where(whereUser,whereCondition).build().list();
    }

    public boolean isTool(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition whereCondition1=AppBeanDao.Properties.IsTool.eq(true);
        AppBean appBean=dao.queryBuilder().orderAsc(AppBeanDao.Properties.Time).where(whereUser,whereCondition,whereCondition1).build().unique();
        return appBean!=null;
    }

    public boolean isExist(String packageName){
        WhereCondition where1= AppBeanDao.Properties.PackageName.eq(packageName);
        AppBean appBean=dao.queryBuilder().where(whereUser,where1).build().unique();
        return appBean!=null;
    }


    public void deleteBean(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        AppBean appBean=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        if (appBean!=null)
            deleteBean(appBean);
    }

    public void deleteBean(AppBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
