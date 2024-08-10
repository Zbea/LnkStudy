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

    public List<PaperTypeBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public List<PaperTypeBean> queryAllByCourse(String course){
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public List<PaperTypeBean> queryAllByCourse(String course,int page, int pageSize){
        WhereCondition whereCondition1= PaperTypeBeanDao.Properties.Course.eq(course);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    public void deleteBean(PaperTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }


}
