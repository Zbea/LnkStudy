package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteContentBeanDao;
import com.bll.lnkstudy.mvp.model.NoteContentBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NoteContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static NoteContentDaoManager mDbController;


    private NoteContentBeanDao noteDao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NoteContentBeanDao.Properties.UserId.eq(userId);

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
        return mDbController;
    }

    public void insertOrReplaceNote(NoteContentBean bean) {
        noteDao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<NoteContentBean> queryList = noteDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<NoteContentBean> queryAll(){
        return noteDao.queryBuilder().where(whereUser).orderDesc(NoteContentBeanDao.Properties.Id).build().list();
    }

    public List<NoteContentBean> queryAll(int type, long notebookId) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookId.eq(notebookId);
        List<NoteContentBean> list = noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        return list;
    }

    public void deleteNote(NoteContentBean noteContent){
        noteDao.delete(noteContent);
    }

    public void deleteType(int type,long notebookId){
        WhereCondition whereCondition=NoteContentBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookId.eq(notebookId);
        List<NoteContentBean> list = noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        noteDao.deleteInTx(list);
    }

}
