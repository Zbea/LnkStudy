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

    public List<ItemTypeBean> queryAll(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderAsc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public Boolean isExist(String title,int type){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Title.eq(title);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1,whereUser2).unique()!=null;
    }

    public void deleteBean(ItemTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
