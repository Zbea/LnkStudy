package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.greendao.PaperTypeBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaperTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaperTypeDaoManager mDbController;
    private final PaperTypeBeanDao dao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public PaperTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaperTypeBeanDao(); //note表
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaperTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaperTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaperTypeDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= PaperTypeBeanDao.Properties.StudentId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaperTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public PaperTypeBean queryById(int id) {
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.TypeId.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().unique();
    }

    public PaperTypeBean queryByName(String name,int grade) {
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Name.eq(name);
        WhereCondition whereCondition2= PaperTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().unique();
    }

    public List<PaperTypeBean> queryAllExceptCloud() {
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.IsCloud.lt(false);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    /**
     * 获取低年级考试卷
     * @param grade
     * @return
     */
    public List<PaperTypeBean> queryAll(int grade) {
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Grade.lt(grade);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public List<PaperTypeBean> queryAllByCourse(String course){
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition1).orderDesc(PaperTypeBeanDao.Properties.Grade).build().list();
    }

    public List<PaperTypeBean> queryAllByCourse(String course,int page, int pageSize){
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .offset((page-1)*pageSize).limit(pageSize).orderDesc(PaperTypeBeanDao.Properties.CreateStatus)
                .build().list();
    }

    public boolean isExistPaperType(int typeId){
        return queryById(typeId)!=null;
    }

    /**
     * 获取当前年级、自动生成的作业本是否已经保存
     * @param name
     * @param course
     * @param grade
     * @return
     */
    public boolean isExistPaperTypeAuto(String name,String course,int grade){
        WhereCondition whereCondition=PaperTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=PaperTypeBeanDao.Properties.Grade.eq(grade);
        WhereCondition whereCondition2=PaperTypeBeanDao.Properties.AutoState.eq(1);
        WhereCondition whereCondition3=PaperTypeBeanDao.Properties.Name.eq(name);
        List<PaperTypeBean> list=dao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2,whereCondition3).build().list();
        return !list.isEmpty();
    }

    public void deleteBean(PaperTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }


}
