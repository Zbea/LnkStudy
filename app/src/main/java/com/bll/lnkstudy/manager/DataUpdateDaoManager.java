package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DataUpdateBeanDao;
import com.bll.lnkstudy.mvp.model.DataUpdateBean;
import com.bll.lnkstudy.mvp.model.User;
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


    private final DataUpdateBeanDao dao;

    private static WhereCondition whereUser;

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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= DataUpdateBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(DataUpdateBean bean) {
        if (bean!=null)
            dao.insertOrReplace(bean);
    }


    public DataUpdateBean queryBean(int type,int id,int contentType,int typeId){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.Uid.eq(id);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        WhereCondition whereCondition4= DataUpdateBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3,whereCondition4).build().unique();
    }

    public DataUpdateBean queryBean(int type,int id,int contentType){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition3= DataUpdateBeanDao.Properties.Uid.eq(id);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.ContentType.eq(contentType);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
    }

    public List<DataUpdateBean> queryList(int type,int typeId){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= DataUpdateBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public List<DataUpdateBean> queryList(int type){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public List<DataUpdateBean> queryList(){
        WhereCondition whereCondition1= DataUpdateBeanDao.Properties.IsUpload.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public void deleteBean(DataUpdateBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
    public void deleteBean(int type,int id,int contentType){
        DataUpdateBean bean=queryBean(type,id,contentType);
        if (bean!=null){
            dao.delete(bean);
        }
    }
    public void deleteBean(int type,int id,int contentType,int typeId){
        DataUpdateBean bean=queryBean(type,id,contentType,typeId);
        if (bean!=null){
            dao.delete(bean);
        }
    }
    public void deleteBeans(int type){
        dao.deleteInTx(queryList(type));
    }

}
