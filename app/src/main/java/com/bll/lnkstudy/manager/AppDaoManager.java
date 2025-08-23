package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.AppBean;

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
        long userId = MethodManager.getAccountId();
        whereUser= AppBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(AppBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<AppBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public AppBean queryBeanByPackageName(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    public AppBean queryBeanByBookId(int bookId){
        WhereCondition whereCondition=AppBeanDao.Properties.BookId.eq(bookId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    public List<AppBean> queryToolAll() {
        WhereCondition whereCondition=AppBeanDao.Properties.IsTool.eq(true);
        return dao.queryBuilder().orderAsc(AppBeanDao.Properties.Time).where(whereUser,whereCondition).build().list();
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
