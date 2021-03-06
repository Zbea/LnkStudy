package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateEventDao;
import com.bll.lnkstudy.greendao.DateEventDao;
import com.bll.lnkstudy.mvp.model.DateEvent;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;


public class DateEventGreenDaoManager {
    
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
    private static DateEventGreenDaoManager mDbController;


    private DateEventDao dateEventDao;  //dateEvent表

    /**
     * 构造初始化
     *
     * @param context
     */
    public DateEventGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dateEventDao = mDaoSession.getDateEventDao();
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
    public static DateEventGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DateEventGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DateEventGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加dateEvent
    public void insertOrReplaceDateEvent(DateEvent bean) {
        dateEventDao.insertOrReplace(bean);
    }

    //根据Id 查询DateEvent
    public DateEvent queryID(Long id) {
        return  dateEventDao.queryBuilder().where(DateEventDao.Properties.Id.eq(id)).build().unique();
    }

    //查询所有当天事件 根据当天时间
    public List<DateEvent> queryAllDateEvent(int type,long dayTim) {
        WhereCondition whereCondition1=DateEventDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=DateEventDao.Properties.DayLong.ge(dayTim);
        List<DateEvent> list = dateEventDao.queryBuilder().where(whereCondition1,whereCondition2).build().list();
        return list;
    }


    public void deleteDateEvent(DateEvent dateEvent){
        dateEventDao.delete(dateEvent);
    }

}
