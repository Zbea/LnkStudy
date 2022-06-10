package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateDayEventDao;
import com.bll.lnkstudy.mvp.model.DateDayEvent;

import java.util.List;


public class DateDayEventGreenDaoManager {


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
    private static DateDayEventGreenDaoManager mDbController;


    private DateDayEventDao dateDayEventDao;  //dateEvent表

    /**
     * 构造初始化
     *
     * @param context
     */
    public DateDayEventGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dateDayEventDao = mDaoSession.getDateDayEventDao();
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
    public static DateDayEventGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DateDayEventGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DateDayEventGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加dateEvent
    public void insertOrReplaceDateDayEvent(DateDayEvent bean) {
        dateDayEventDao.insertOrReplace(bean);
    }

    //根据Id 查询DateEvent
    public DateDayEvent queryID(Long id) {
        return  dateDayEventDao.queryBuilder().where(DateDayEventDao.Properties.Id.eq(id)).build().unique();
    }

    //查询所有当天事件 根据当天时间
    public List<DateDayEvent> queryAllDateDayEvent(long dayTim) {
        List<DateDayEvent> list = dateDayEventDao.queryBuilder().where(DateDayEventDao.Properties.DayLong.ge(dayTim)).build().list();
        return list;
    }


    //删除书籍数据d对象
    public void deleteDateDayEvent(DateDayEvent dateDayEvent){
        dateDayEventDao.delete(dateDayEvent);
    }

}
