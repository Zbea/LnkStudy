package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DataUpdateBeanDao;
import com.bll.lnkstudy.greendao.PaperTypeBeanDao;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.DataUpdateBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.RecordBean;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class DataUpdateDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static DataUpdateDaoManager mDbController;


    private DataUpdateBeanDao dao;

    private long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    private WhereCondition whereUser= DataUpdateBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public DataUpdateDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getDataUpdateBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static DataUpdateDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (DataUpdateDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DataUpdateDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(DataUpdateBean bean) {
        dao.insertOrReplace(bean);
    }

    public DataUpdateBean queryBean(int type,int contentType,int id){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.Uid.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
    }
    public DataUpdateBean queryBean(int type,int contentType,int id,int typeId){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.Uid.eq(id);
        WhereCondition whereCondition4= DataUpdateBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3,whereCondition4).build().unique();
    }

    public List<DataUpdateBean> queryList(int type,int contentType,int id){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.Uid.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().list();
    }

    public List<DataUpdateBean> queryList(int type,int contentType,int id,int typeId){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.Uid.eq(id);
        WhereCondition whereCondition4= DataUpdateBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3,whereCondition4).build().list();
    }

    public List<DataUpdateBean> queryList(int type){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public List<DataUpdateBean> queryList(int type,int typeId){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public List<DataUpdateBean> queryList(long startDate,long endDate){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Date.between(startDate,endDate);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public void deleteBean(DataUpdateBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

    public void deleteBeans(int type){
        dao.deleteInTx(queryList(type));
    }

    public void deleteBeans(int type,int typeId){
        dao.deleteInTx(queryList(type,typeId));
    }

}
