package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class RecordDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static RecordDaoManager mDbController;
    private final RecordBeanDao recordBeanDao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public RecordDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        recordBeanDao = mDaoSession.getRecordBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static RecordDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (RecordDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new RecordDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= RecordBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(RecordBean bean) {
        recordBeanDao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(RecordBean bean) {
        recordBeanDao.insertOrReplace(bean);
        List<RecordBean> queryList = recordBeanDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    /**
     * 随笔录音
     * @return
     */
    public List<RecordBean> queryAllRecord() {
        WhereCondition whereCondition1=RecordBeanDao.Properties.TypeId.eq(0);
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
    }

    public List<RecordBean> queryAllRecord(int page, int pageSize) {
        WhereCondition whereCondition1=RecordBeanDao.Properties.TypeId.eq(0);
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(RecordBeanDao.Properties.Date).offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    /**
     * 作业录音
     * @param course
     * @param typeId
     * @return
     */
    public List<RecordBean> queryAllByCourse(String course,int typeId) {
        WhereCondition whereCondition=RecordBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=RecordBeanDao.Properties.TypeId.eq(typeId);
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
    }

    /**
     * 作业录音
     * @param course
     * @param typeId
     * @return
     */
    public List<RecordBean> queryAllByCourse(String course,int typeId,int page, int pageSize) {
        WhereCondition whereCondition=RecordBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=RecordBeanDao.Properties.TypeId.eq(typeId);
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .orderDesc(RecordBeanDao.Properties.Date).offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public List<RecordBean> search(String title) {
        WhereCondition whereCondition=RecordBeanDao.Properties.Title.like("%"+title+"%");
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
    }

    public void deleteBean(RecordBean bean){
        recordBeanDao.delete(bean);
    }

    public void clear(){
        recordBeanDao.deleteAll();
    }

}
