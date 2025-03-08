package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkPaperBeanDao;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

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

    /**
     *
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<HomeworkPaperBean> queryAll(String course, int categoryId) {
        WhereCondition whereCondition1= HomeworkPaperBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= HomeworkPaperBeanDao.Properties.HomeworkTypeId.eq(categoryId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public List<HomeworkPaperBean> search(String title) {
        WhereCondition whereCondition1= HomeworkPaperBeanDao.Properties.Title.like("%"+title+"%");
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public void deleteBean(HomeworkPaperBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
