package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DatePlanEventDao;
import com.bll.lnkstudy.mvp.model.DatePlanEvent;

import java.util.List;


public class DatePlanEventGreenDaoManager {

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
    private static DatePlanEventGreenDaoManager mDbController;


    private DatePlanEventDao datePlanEventDao;  //dateEvent表

    /**
     * 构造初始化
     *
     * @param context
     */
    public DatePlanEventGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        datePlanEventDao = mDaoSession.getDatePlanEventDao();
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
    public static DatePlanEventGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DatePlanEventGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DatePlanEventGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加dateEvent
    public void insertOrReplaceDatePlanEvent(DatePlanEvent bean) {
        datePlanEventDao.insertOrReplace(bean);
    }

    //查询所有当天事件 根据当天时间
    public List<DatePlanEvent> queryAllDatePlanEvent(long dayTim) {
        List<DatePlanEvent> list = datePlanEventDao.queryBuilder().where(DatePlanEventDao.Properties.DayLong.eq(dayTim))
                .orderAsc(DatePlanEventDao.Properties.StartTime)
                .build().list();
        return list;
    }
    //通过id查询事件
    public DatePlanEvent queryDatePlanEvent(long id){
        return datePlanEventDao.loadByRowId(id);
    }

    public void deleteDatePlanEvent(DatePlanEvent datePlanEvent){
        datePlanEventDao.delete(datePlanEvent);
    }

}
