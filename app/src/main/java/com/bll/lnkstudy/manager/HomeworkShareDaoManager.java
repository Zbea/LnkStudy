package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkPaperBeanDao;
import com.bll.lnkstudy.greendao.HomeworkShareBeanDao;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkShareDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkShareDaoManager mDbController;
    private final HomeworkShareBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkShareDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getHomeworkShareBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkShareDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkShareDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkShareDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= HomeworkShareBeanDao.Properties.StudentId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkShareBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(HomeworkShareBean bean) {
        dao.insertOrReplace(bean);
        List<HomeworkShareBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<HomeworkShareBean> queryAll(int typeId) {
        WhereCondition whereCondition1= HomeworkShareBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1).orderAsc(HomeworkShareBeanDao.Properties.Date).build().list();
    }

    public void deleteBean(HomeworkShareBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
