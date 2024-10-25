package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.ItemTypeBeanDao;
import com.bll.lnkstudy.mvp.model.ItemTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class ItemTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static ItemTypeDaoManager mDbController;
    private final ItemTypeBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public ItemTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getItemTypeBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static ItemTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (ItemTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new ItemTypeDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= ItemTypeBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(ItemTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(ItemTypeBean bean) {
        dao.insertOrReplace(bean);
        List<ItemTypeBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public ItemTypeBean queryBean(int type,int grade) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereUser1,whereUser2).orderAsc(ItemTypeBeanDao.Properties.Date).build().unique();
    }

    public void saveBookBean(int type,String title,boolean isNew) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Title.eq(title);
        ItemTypeBean bean=dao.queryBuilder().where(whereUser,whereUser1,whereUser2).orderAsc(ItemTypeBeanDao.Properties.Date).build().unique();
        bean.setIsNew(isNew);
        insertOrReplace(bean);
    }

    public Boolean isExistBookType(){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.IsNew.eq(true);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(5);
        return !dao.queryBuilder().where(whereUser, whereUser1, whereUser2).build().list().isEmpty();
    }

    public List<ItemTypeBean> queryAll(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderAsc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    /**
     * 降序
     * @param type
     * @return
     */
    public List<ItemTypeBean> queryAllOrderDesc(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderDesc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public Boolean isExist(String title,int type){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Title.eq(title);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(type);
        return !dao.queryBuilder().where(whereUser,whereUser1,whereUser2).build().list().isEmpty();
    }

    /**
     * 查看日记分类是否已经下载
     * @param typeId
     * @return
     */
    public Boolean isExistDiaryType(int typeId){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.TypeId.eq(typeId);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(6);
        return !dao.queryBuilder().where(whereUser, whereUser1, whereUser2).build().list().isEmpty();
    }

    public void deleteBean(ItemTypeBean bean){
        dao.delete(bean);
    }

    public void clear(int type){
        dao.deleteInTx(queryAll(type));
    }

    public void clear(){
        dao.deleteAll();
    }

}
