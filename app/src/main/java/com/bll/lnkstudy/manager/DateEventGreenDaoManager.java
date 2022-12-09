package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateEventDao;
import com.bll.lnkstudy.mvp.model.DateEvent;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;


public class DateEventGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static DateEventGreenDaoManager mDbController;


    private DateEventDao dateEventDao;  //dateEvent表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= DateEventDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public DateEventGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dateEventDao = mDaoSession.getDateEventDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static DateEventGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (DateEventGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DateEventGreenDaoManager();
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
        return  dateEventDao.queryBuilder().where(whereUser,DateEventDao.Properties.Id.eq(id)).build().unique();
    }

    //查询所有当天事件 根据当天时间
    public List<DateEvent> queryAllDateEvent(int type,long dayTim) {
        WhereCondition whereCondition1=DateEventDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=DateEventDao.Properties.DayLong.ge(dayTim);
        List<DateEvent> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventDao.Properties.Id).build().list();
        return list;
    }

    //查询当天所有已过期日程
    public List<DateEvent> queryAllDateEvent1(int type,long dayTim) {
        WhereCondition whereCondition1=DateEventDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=DateEventDao.Properties.DayLong.lt(dayTim);
        List<DateEvent> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventDao.Properties.Id).build().list();
        return list;
    }

    //查询所有
    public List<DateEvent> queryAllDateEvent(int type) {
        WhereCondition whereCondition1=DateEventDao.Properties.Type.eq(type);
        List<DateEvent> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1).orderDesc(DateEventDao.Properties.Id).build().list();
        return list;
    }

    //查询当天重要日子
    public List<DateEvent> queryAllDateEvent(long dayTim) {
        WhereCondition whereCondition1=DateEventDao.Properties.Type.eq(1);
        WhereCondition whereCondition2=DateEventDao.Properties.DayLong.eq(dayTim);
        List<DateEvent> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventDao.Properties.Id).build().list();
        return list;
    }


    public void deleteDateEvent(DateEvent dateEvent){
        dateEventDao.delete(dateEvent);
    }

}
