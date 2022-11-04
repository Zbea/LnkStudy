package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class AppDaoManager {
    /**
     * 数据库名字
     */
    private String DB_NAME = "plan.db";  //数据库名字
    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private static AppDaoManager mDbController;


    private AppBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= AppBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     *
     * @param context
     */
    public AppDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dao = mDaoSession.getAppBeanDao();
    }

    /**
     * 获取可写数据库
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        }
        db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static AppDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (AppDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new AppDaoManager(context);
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

}
