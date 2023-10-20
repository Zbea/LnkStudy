package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NotebookDao;
import com.bll.lnkstudy.mvp.model.note.Notebook;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class NotebookDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static NotebookDaoManager mDbController;
    private final NotebookDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public NotebookDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= NotebookDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(Notebook bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(Notebook bean) {
        dao.insertOrReplace(bean);
        List<Notebook> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<Notebook> queryAll() {
        return dao.queryBuilder().where(whereUser)
                .orderAsc(NotebookDao.Properties.Date).build().list();
    }

    /**
     * 是否存在这个分类
     * @param title
     * @return
     */
    public Boolean isExist(String title){
        WhereCondition whereUser1= NotebookDao.Properties.Name.eq(title);
        return dao.queryBuilder().where(whereUser,whereUser1).unique()!=null;
    }

    public void deleteBean(Notebook bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }


}
