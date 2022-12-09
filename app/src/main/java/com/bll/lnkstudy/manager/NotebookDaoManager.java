package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NotebookDao;
import com.bll.lnkstudy.mvp.model.Notebook;
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

    private NotebookDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NotebookDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NotebookDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNotebookDao();
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

    public void insertOrReplace(Notebook bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<Notebook> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    /**
     * @return
     */
    public List<Notebook> queryAll() {
        WhereCondition whereCondition=NotebookDao.Properties.Type.gt(0);
        List<Notebook> queryList = dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookDao.Properties.CreateDate).build().list();
        return queryList;
    }

    /**
     * @return
     */
    public List<Notebook> queryAll(int type) {
        WhereCondition whereCondition=NotebookDao.Properties.Type.eq(type);
        List<Notebook> queryList = dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookDao.Properties.CreateDate).build().list();
        return queryList;
    }

    /**
     * @return
     */
    public List<Notebook> queryAll(int type,int page, int pageSize) {
        WhereCondition whereCondition=NotebookDao.Properties.Type.eq(type);
        List<Notebook> queryList = dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookDao.Properties.CreateDate)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
        return queryList;
    }

    public void deleteBean(Notebook bean){
        dao.delete(bean);
    }

}
