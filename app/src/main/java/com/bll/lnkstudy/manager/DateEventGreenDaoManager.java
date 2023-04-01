package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateEventBeanDao;
import com.bll.lnkstudy.mvp.model.date.DateEventBean;
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


    private DateEventBeanDao dateEventDao;  //dateEvent表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= DateEventBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public DateEventGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dateEventDao = mDaoSession.getDateEventBeanDao();
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
    public void insertOrReplaceDateEvent(DateEventBean bean) {
        dateEventDao.insertOrReplace(bean);
    }

    //根据Id 查询DateEvent
    public DateEventBean queryID(Long id) {
        return  dateEventDao.queryBuilder().where(whereUser,DateEventBeanDao.Properties.Id.eq(id)).build().unique();
    }

    //查询所有当天事件 根据当天时间
    public List<DateEventBean> queryAllDateEvent(int type, long dayTim) {
        WhereCondition whereCondition1=DateEventBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=DateEventBeanDao.Properties.DayLong.ge(dayTim);
        List<DateEventBean> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventBeanDao.Properties.Id).build().list();
        return list;
    }

    //查询当天所有已过期日程
    public List<DateEventBean> queryAllDateEvent1(int type, long dayTim) {
        WhereCondition whereCondition1=DateEventBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=DateEventBeanDao.Properties.DayLong.lt(dayTim);
        List<DateEventBean> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventBeanDao.Properties.Id).build().list();
        return list;
    }

    //查询所有
    public List<DateEventBean> queryAllDateEvent(int type) {
        WhereCondition whereCondition1=DateEventBeanDao.Properties.Type.eq(type);
        List<DateEventBean> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1).orderDesc(DateEventBeanDao.Properties.Id).build().list();
        return list;
    }

    //查询当天重要日子
    public List<DateEventBean> queryAllDateEvent(long dayTim) {
        WhereCondition whereCondition1=DateEventBeanDao.Properties.Type.eq(1);
        WhereCondition whereCondition2=DateEventBeanDao.Properties.DayLong.eq(dayTim);
        List<DateEventBean> list = dateEventDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderDesc(DateEventBeanDao.Properties.Id).build().list();
        return list;
    }


    public void deleteDateEvent(DateEventBean dateEventBean){
        dateEventDao.delete(dateEventBean);
    }

}
