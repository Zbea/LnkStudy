package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean;
import com.bll.lnkstudy.mvp.model.homework.RecordBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class RecordDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static RecordDaoManager mDbController;


    private RecordBeanDao recordBeanDao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= RecordBeanDao.Properties.UserId.eq(userId);

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

    public List<RecordBean> queryAllByCourse(String course,int typeId) {
        WhereCondition whereCondition=RecordBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=RecordBeanDao.Properties.TypeId.eq(typeId);
        return recordBeanDao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
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
