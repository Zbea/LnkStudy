package com.bll.lnkstudy.manager;

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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= HomeworkTypeBeanDao.Properties.StudentId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public HomeworkTypeBean queryById(int typeId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    /**
     * 查找老师作业本
     * @param typeId
     * @return
     */
    public HomeworkTypeBean queryByTypeId(int typeId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(typeId);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.FromStatus.eq(2);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    /**
     * 查找家长作业本
     * @param typeId
     * @return
     */
    public HomeworkTypeBean queryByParentTypeId(int typeId) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.TypeId.eq(typeId);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.FromStatus.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    public HomeworkTypeBean queryByName(String name,String course,int grade) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.Grade.eq(grade);
        WhereCondition whereCondition2=HomeworkTypeBeanDao.Properties.Name.eq(name);
        WhereCondition whereCondition3=HomeworkTypeBeanDao.Properties.CreateStatus.eq(2);
        WhereCondition whereCondition4=HomeworkTypeBeanDao.Properties.AutoState.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2,whereCondition3,whereCondition4).build().unique();
    }

    /**
     * 查找当前科目、所有作业本
     * @param course
     * @return
     */
    public List<HomeworkTypeBean> queryAllByCourse(String course) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course,int page, int pageSize) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition)
                .offset((page-1)*pageSize).limit(pageSize).orderDesc(HomeworkTypeBeanDao.Properties.CreateStatus).orderAsc(HomeworkTypeBeanDao.Properties.Date)
                .build().list();
    }

    public List<HomeworkTypeBean> queryAllByCreate(String course,int create,int autoState){
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.CreateStatus.eq(create);
        WhereCondition whereCondition2=HomeworkTypeBeanDao.Properties.AutoState.eq(autoState);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2)
                .build().list();
    }


    public List<HomeworkTypeBean> queryAllByLocal(String course){
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.CreateStatus.eq(0);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .build().list();
    }

    /**
     * 查找科目下 不同类型的作业本
     */
    public List<HomeworkTypeBean> queryAllByState(String course,int grade,int state) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.State.eq(state);
        WhereCondition whereCondition2=HomeworkTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2).build().list();
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

    public HomeworkTypeBean queryByNameGrade(String name,int grade){
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Name.eq(name);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
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
    public boolean isExistType(int typeId) {
        return queryById(typeId)!=null;
    }

    /**
     * 是否存在作业本
     * @param typeId
     * @return
     */
    public boolean isExistHomeworkType(int typeId) {
        return queryByTypeId(typeId)!=null;
    }

    /**
     * 是否存在家长本子
     * @param parentId
     * @return
     */
    public boolean isExistParentType(int parentId) {
        return queryByParentTypeId(parentId)!=null;
    }

    public List<HomeworkTypeBean> queryAllExceptCloud() {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.IsCloud.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     * 获取低年级的所有作业本
     * @param grade
     * @return
     */
    public List<HomeworkTypeBean> queryAll(int grade) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Grade.lt(grade);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public void deleteBean(HomeworkTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
