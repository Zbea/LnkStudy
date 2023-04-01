package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NotebookBeanDao;
import com.bll.lnkstudy.mvp.model.NotebookBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NotebookDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static NotebookDaoManager mDbController;

    private NotebookBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NotebookBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NotebookDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNotebookBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NotebookDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NotebookDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NotebookDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(NotebookBean bean) {
        dao.insertOrReplace(bean);
    }

    /**
     * @return
     */
    public List<NotebookBean> queryAll() {
        WhereCondition whereCondition=NotebookBeanDao.Properties.Type.gt(0);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookBeanDao.Properties.CreateDate).build().list();
    }

    /**
     * @return
     */
    public List<NotebookBean> queryAll(int type) {
        WhereCondition whereCondition=NotebookBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookBeanDao.Properties.CreateDate).build().list();
    }

    /**
     * @return
     */
    public List<NotebookBean> queryAll(int type, int page, int pageSize) {
        WhereCondition whereCondition=NotebookBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookBeanDao.Properties.CreateDate)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public void deleteBean(NotebookBean bean){
        dao.delete(bean);
    }

}
