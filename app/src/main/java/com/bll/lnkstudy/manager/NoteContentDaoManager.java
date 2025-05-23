package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteContentBeanDao;
import com.bll.lnkstudy.mvp.model.note.NoteContentBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.FileUtils;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class NoteContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static NoteContentDaoManager mDbController;
    private final NoteContentBeanDao noteDao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public NoteContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        noteDao = mDaoSession.getNoteContentBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteContentDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= NoteContentBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    public void insertOrReplaceNote(NoteContentBean bean) {
        noteDao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(NoteContentBean bean) {
        noteDao.insertOrReplace(bean);
        List<NoteContentBean> queryList = noteDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public List<NoteContentBean> queryAll(String type, String notebookStr) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NoteTitle.eq(notebookStr);
        return noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderAsc(NoteContentBeanDao.Properties.Date).build().list();
    }

    public List<NoteContentBean> search(String title) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.Title.like("%"+title+"%");
        return noteDao.queryBuilder().where(whereUser,whereCondition).orderAsc(NoteContentBeanDao.Properties.Date).build().list();
    }

    public void deleteType(String type,String notebookStr){
        List<NoteContentBean> list = queryAll(type, notebookStr);
        noteDao.deleteInTx(list);
    }

    public void clear(){
        noteDao.deleteAll();
    }

}
