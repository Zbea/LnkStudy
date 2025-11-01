package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkPaperBeanDao;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class HomeworkTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkTypeDaoManager mDbController;
    private final HomeworkTypeBeanDao dao;  //note表
    private static WhereCondition whereUser;

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
        long userId = MethodManager.getAccountId();
        whereUser= HomeworkTypeBeanDao.Properties.StudentId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkTypeBean bean) {
        dao.insertOrReplace(bean);
    }
    /**
     * 查找老师作业本
     * @param typeId
     * @return
     */
    public HomeworkTypeBean queryByTypeId(int typeId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(typeId);
        List<HomeworkTypeBean> homeworkTypeBeans=dao.queryBuilder().where(whereUser,whereCondition).build().list();
        if (!homeworkTypeBeans.isEmpty()) {
            return homeworkTypeBeans.get(homeworkTypeBeans.size()-1);
        }
        return null;
    }

    /**
     * 查找题卷本
     * @param bookId
     * @return
     */
    public HomeworkTypeBean queryByBookId(int bookId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.BookId.eq(bookId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    /**
     * 获取老师 自动生成作业本
     * @param name
     * @param course
     * @param grade
     * @return
     */
    public HomeworkTypeBean queryByAutoName(String name,String course,int grade) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.Grade.eq(grade);
        WhereCondition whereCondition2=HomeworkTypeBeanDao.Properties.Name.eq(name);
        WhereCondition whereCondition3=HomeworkTypeBeanDao.Properties.FromStatus.eq(2);
        WhereCondition whereCondition4=HomeworkTypeBeanDao.Properties.AutoState.eq(1);
        List<HomeworkTypeBean> homeworkTypeBeans=dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2,whereCondition3,whereCondition4).build().list();
        if (!homeworkTypeBeans.isEmpty()) {
            return homeworkTypeBeans.get(homeworkTypeBeans.size()-1);
        }
        return null;
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course,int page, int pageSize) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition)
                .offset((page-1)*pageSize).limit(pageSize).orderDesc(HomeworkTypeBeanDao.Properties.CreateStatus).orderDesc(HomeworkTypeBeanDao.Properties.AutoState).orderDesc(HomeworkTypeBeanDao.Properties.State).orderAsc(HomeworkTypeBeanDao.Properties.Date)
                .build().list();
    }

    public List<HomeworkTypeBean> queryAllByCreate(String course,int create){
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.CreateStatus.eq(create);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .build().list();
    }

    public List<HomeworkTypeBean> queryAllByCreate(int create){
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.CreateStatus.eq(create);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .build().list();
    }

    /**
     * 查找科目下 本地题卷本
     */
    public List<HomeworkTypeBean> queryAllByBook(String course,int grade) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.State.eq(4);
        WhereCondition whereCondition2=HomeworkTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2).build().list();
    }

    /**
     * 获取当前年级之前的所有未上传作业本
     * @return
     */
    public List<HomeworkTypeBean> queryAllExceptCloud(int grade) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.IsCloud.eq(false);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.Grade.lt(grade);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
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
     * 是否存在作业本
     * @param typeId
     * @return
     */
    public boolean isExistHomeworkType(int typeId) {
        return queryByTypeId(typeId)!=null;
    }

    public void deleteBean(int typeId){
        dao.delete(queryByTypeId(typeId));
    }

    public void deleteBean(HomeworkTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
