package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteTypeBeanDao;
import com.bll.lnkstudy.greendao.NotebookBeanDao;
import com.bll.lnkstudy.mvp.model.NoteTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NoteTypeBeanDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static NoteTypeBeanDaoManager mDbController;


    private NoteTypeBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NoteTypeBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NoteTypeBeanDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNoteTypeBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteTypeBeanDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteTypeBeanDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteTypeBeanDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(NoteTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<NoteTypeBean> queryAll() {
        List<NoteTypeBean> queryList = dao.queryBuilder().where(whereUser)
                .orderAsc(NoteTypeBeanDao.Properties.Date).build().list();
        return queryList;
    }

    /**
     * 是否存在这个分类
     * @param title
     * @return
     */
    public Boolean isExist(String title){
        WhereCondition whereUser1= NoteTypeBeanDao.Properties.Name.eq(title);
        return dao.queryBuilder().where(whereUser,whereUser1).unique()!=null;
    }

    public void deleteBean(NoteTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }


}
