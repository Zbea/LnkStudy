package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.BaseTypeBeanDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.BaseTypeBean;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class BaseTypeBeanDaoManager {


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
    private static BaseTypeBeanDaoManager mDbController;


    private BaseTypeBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;

    /**
     * 构造初始化
     *
     * @param context
     */
    public BaseTypeBeanDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dao = mDaoSession.getBaseTypeBeanDao();
    }


    /**
     * 获取可写数据库
     *
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
    public static BaseTypeBeanDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (BaseTypeBeanDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BaseTypeBeanDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(BaseTypeBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<BaseTypeBean> queryAll() {
        WhereCondition whereCondition=BaseTypeBeanDao.Properties.UserId.eq(userId);
        List<BaseTypeBean> queryList = dao.queryBuilder().where(whereCondition)
                .orderAsc(BaseTypeBeanDao.Properties.Date).build().list();
        return queryList;
    }

    public void deleteBean(BaseTypeBean bean){
        dao.delete(bean);
    }


}
