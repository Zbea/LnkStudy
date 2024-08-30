package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DiaryBeanDao;
import com.bll.lnkstudy.greendao.FreeNoteBeanDao;
import com.bll.lnkstudy.mvp.model.DiaryBean;
import com.bll.lnkstudy.mvp.model.FreeNoteBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiaryDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static DiaryDaoManager mDbController;
    private final DiaryBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public DiaryDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getDiaryBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static DiaryDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (DiaryDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DiaryDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= DiaryBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(DiaryBean bean) {
        dao.insertOrReplace(bean);
    }

    public DiaryBean queryBean(long time) {
        WhereCondition whereCondition= DiaryBeanDao.Properties.Date.eq(time);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(DiaryBeanDao.Properties.Date).build().unique();
    }

    /**
     * 查找除开当前时间最近的日期
     * @param time
     * @param type 0小于当前时间 1大于当前时间
     * @return
     */
    public DiaryBean queryBean(long time,int type){
        WhereCondition whereCondition;
        DiaryBean diaryBean;
        if (type==0){
            whereCondition= DiaryBeanDao.Properties.Date.lt(time);
            diaryBean=dao.queryBuilder().where(whereUser,whereCondition).orderDesc(DiaryBeanDao.Properties.Date).limit(1).build().unique();
        }
        else {
            whereCondition= DiaryBeanDao.Properties.Date.gt(time);
            diaryBean=dao.queryBuilder().where(whereUser,whereCondition).orderAsc(DiaryBeanDao.Properties.Date).limit(1).build().unique();
        }
        return diaryBean;
    }

    public List<DiaryBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(DiaryBeanDao.Properties.Date).build().list();
    }

    public List<DiaryBean> queryList(long startLong,long endLong) {
        WhereCondition whereCondition= DiaryBeanDao.Properties.Date.between(startLong,endLong);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(DiaryBeanDao.Properties.Date).build().list();
    }

    public List<DiaryBean> queryListByTitle() {
        WhereCondition whereCondition= DiaryBeanDao.Properties.Title.isNotNull();
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(DiaryBeanDao.Properties.Date).build().list();
    }
    public List<Long> queryLongList(int year,int month) {
        List<Long> times=new ArrayList<>();
        WhereCondition whereCondition= DiaryBeanDao.Properties.Year.eq(year);
        WhereCondition whereCondition1= DiaryBeanDao.Properties.Month.eq(month);
        List<DiaryBean> beans=dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderDesc(DiaryBeanDao.Properties.Date).build().list();
        for (DiaryBean item :beans) {
            times.add(item.date);
        }
        return times;
    }

    public void delete(DiaryBean item){
        dao.delete(item);
    }

    public void clear(){
        dao.deleteAll();
    }

}
