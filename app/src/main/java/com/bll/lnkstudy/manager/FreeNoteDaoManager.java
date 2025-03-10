package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.FreeNoteBeanDao;
import com.bll.lnkstudy.greendao.HomeworkContentBeanDao;
import com.bll.lnkstudy.mvp.model.FreeNoteBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class FreeNoteDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static FreeNoteDaoManager mDbController;
    private final FreeNoteBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public FreeNoteDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getFreeNoteBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static FreeNoteDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (FreeNoteDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new FreeNoteDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= FreeNoteBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(FreeNoteBean bean) {
        dao.insertOrReplace(bean);
    }

    public FreeNoteBean queryBean() {
        WhereCondition whereCondition= FreeNoteBeanDao.Properties.IsSave.eq(false);
        List<FreeNoteBean> items=dao.queryBuilder().where(whereUser,whereCondition).orderDesc(FreeNoteBeanDao.Properties.Date).build().list();
        if (items.size()==0){
            return null;
        }
        if (items.size()>1){
            for (int i = 1; i < items.size()-1; i++) {
                FreeNoteBean item= items.get(i);
                item.isSave=true;
                insertOrReplace(item);
            }
        }
        return items.get(0);
    }

    public List<FreeNoteBean> queryList( int page, int pageSize) {
        return dao.queryBuilder().where(whereUser).orderDesc(FreeNoteBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public List<FreeNoteBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(FreeNoteBeanDao.Properties.Date).build().list();
    }

    public void deleteBean(FreeNoteBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
