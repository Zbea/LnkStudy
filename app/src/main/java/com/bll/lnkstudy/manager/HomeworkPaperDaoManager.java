package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkPaperBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkPaperDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkPaperDaoManager mDbController;
    private final HomeworkPaperBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkPaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getHomeworkPaperBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkPaperDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkPaperDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkPaperDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= HomeworkPaperBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkPaperBean bean) {
        dao.insertOrReplace(bean);
    }

    public HomeworkPaperBean queryByContentID(int id) {
        return dao.queryBuilder().where(whereUser,HomeworkPaperBeanDao.Properties.ContentId.eq(id)).build().unique();
    }

    public List<HomeworkPaperBean> queryAll(String course, int categoryId) {
        WhereCondition whereCondition1= HomeworkPaperBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= HomeworkPaperBeanDao.Properties.HomeworkTypeId.eq(categoryId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).orderAsc(HomeworkPaperBeanDao.Properties.Date).build().list();
    }

    public List<HomeworkPaperBean> queryAllByLocal(String course, int categoryId) {
        WhereCondition whereCondition1= HomeworkPaperBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= HomeworkPaperBeanDao.Properties.HomeworkTypeId.eq(categoryId);
        WhereCondition whereCondition3= HomeworkPaperBeanDao.Properties.IsHomework.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).orderAsc(HomeworkPaperBeanDao.Properties.Date).build().list();
    }

    public List<HomeworkPaperBean> search(String title) {
        WhereCondition whereCondition1= HomeworkPaperBeanDao.Properties.Title.like("%"+title+"%");
        return dao.queryBuilder().where(whereUser,whereCondition1).orderDesc(HomeworkPaperBeanDao.Properties.Date).build().list();
    }

    public void deleteBean(HomeworkPaperBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
