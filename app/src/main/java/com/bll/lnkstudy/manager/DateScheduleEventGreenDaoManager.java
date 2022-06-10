package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateScheduleEventDao;
import com.bll.lnkstudy.mvp.model.DateScheduleEvent;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;


public class DateScheduleEventGreenDaoManager {


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
    private static DateScheduleEventGreenDaoManager mDbController;


    private DateScheduleEventDao dateEventDao;  //dateEvent表

    /**
     * 构造初始化
     *
     * @param context
     */
    public DateScheduleEventGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        dateEventDao = mDaoSession.getDateScheduleEventDao();
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
    public static DateScheduleEventGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DateScheduleEventGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DateScheduleEventGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加dateEvent
    public void insertOrReplaceDateEvent(DateScheduleEvent bean) {
        dateEventDao.insertOrReplace(bean);
    }


    //根据Id 查询DateEvent
    public DateScheduleEvent queryDateEventByDateEventID(Long id) {
        return  dateEventDao.queryBuilder().where(DateScheduleEventDao.Properties.Id.eq(id)).build().unique();
    }


    //查询所有当天事件 根据当天时间
    public List<DateScheduleEvent> queryAllDateEvent(long dayTim) {
        WhereCondition whereCondition=DateScheduleEventDao.Properties.ScheduleEndTime.ge(""+dayTim);
        List<DateScheduleEvent> list = dateEventDao.queryBuilder().where(whereCondition)
                .orderAsc(DateScheduleEventDao.Properties.ScheduleStartTime).build().list();
        return list;
    }


    public void deleteDateEvent(DateScheduleEvent dateScheduleEvent){
        dateEventDao.delete(dateScheduleEvent);
    }

}
