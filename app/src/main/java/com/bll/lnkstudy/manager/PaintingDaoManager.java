package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkDao;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.mvp.model.Homework;
import com.bll.lnkstudy.mvp.model.PaintingBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaintingDaoManager {
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
    private static PaintingDaoManager mDbController;


    private PaintingBeanDao dao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public PaintingDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dao = mDaoSession.getPaintingBeanDao();
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
    public static PaintingDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (PaintingDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaintingBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<PaintingBean> queryAllByType(int type) {
        WhereCondition whereCondition=PaintingBeanDao.Properties.Type.eq(type);
        List<PaintingBean> queryList = dao.queryBuilder().where(whereCondition).build().list();
        return queryList;
    }

    public void deleteBean(PaintingBean bean){
        dao.delete(bean);
    }


}
