package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkTypeDaoManager mDbController;


    private HomeworkTypeBeanDao dao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkTypeBeanDao.Properties.StudentId.eq(userId);

    /**
     * 构造初始化
     */
    public HomeworkTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkTypeBeanDao(); //note表
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkTypeDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkTypeBean bean) {
        dao.insertOrReplace(bean);
    }
    public HomeworkTypeBean queryByTypeId(int typeId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    /**
     * 查找往期作业本
     * @param isCloud
     * @return
     */
    public List<HomeworkTypeBean> queryAllByCloud(boolean isCloud) {
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCloud.eq(isCloud);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    /**
     * 查找往期作业本
     * @param isCloud
     * @return
     */
    public List<HomeworkTypeBean> queryAllByCloud(boolean isCloud,int page, int pageSize) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.IsCloud.eq(isCloud);
        return  dao.queryBuilder().where(whereUser,whereCondition)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCloud.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course,int page, int pageSize) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCloud.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    /**
     * 查找作业本
     */
    public List<HomeworkTypeBean> queryAllByState(String course,int state) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.State.eq(state);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
    }

    public HomeworkTypeBean queryAllById(int id) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    /**
     * 是否存在题卷本
     * @param bookId
     * @return
     */
    public HomeworkTypeBean queryByBookId(int bookId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.BookId.eq(bookId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    /**
     * 是否存在题卷本
     * @param bookId
     * @return
     */
    public boolean isExistHomeworkTypeBook(int bookId) {
        return queryByBookId(bookId)!=null;
    }

    /**
     * 是否存在本子
     * @param typeId
     * @return
     */
    public boolean isExistHomeworkType(int typeId) {
        return queryAllById(typeId)!=null;
    }

    public List<HomeworkTypeBean> queryAllByBook() {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.BookId.notEq(0);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     * 获取所有作业（不包括从云书库下载的）
     * @return
     */
    public List<HomeworkTypeBean> queryAllExcludeCloud() {
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCloud.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public List<HomeworkTypeBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public void deleteBean(HomeworkTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
