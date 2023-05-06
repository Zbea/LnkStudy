package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkContentBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkContentDaoManager {


    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkContentDaoManager mDbController;


    private HomeworkContentBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkContentBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public HomeworkContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkContentBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkContentDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkContentBean bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<HomeworkContentBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public List<HomeworkContentBean> queryAllByType(String course, int homeworkTypeId) {

        WhereCondition whereCondition= HomeworkContentBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkContentBeanDao.Properties.HomeworkTypeId.eq(homeworkTypeId);

        List<HomeworkContentBean> queryList = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
//                .orderDesc(HomeworkContentDao.Properties.Date).build().list();
        return queryList;
    }

    public List<HomeworkContentBean> queryAllById(int contentId) {
        WhereCondition whereCondition=HomeworkContentBeanDao.Properties.ContentId.eq(contentId);
        List<HomeworkContentBean> queryList = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return queryList;
    }


    public void deleteAll(String course, int homeworkTypeId) {

        WhereCondition whereCondition= HomeworkContentBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkContentBeanDao.Properties.HomeworkTypeId.eq(homeworkTypeId);
        List<HomeworkContentBean> queryList = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();

        dao.deleteInTx(queryList);
    }


    public void deleteBean(HomeworkContentBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
