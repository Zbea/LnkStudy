package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteContentDao;
import com.bll.lnkstudy.mvp.model.NoteContent;
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


    private NoteContentDao noteDao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NoteContentDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NoteContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        noteDao = mDaoSession.getNoteContentDao(); //note表
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

    public void insertOrReplaceNote(NoteContent bean) {
        noteDao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<NoteContent> queryList = noteDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<NoteContent> queryAll(){
        return noteDao.queryBuilder().where(whereUser).orderDesc(NoteContentDao.Properties.Id).build().list();
    }

    public List<NoteContent> queryAll(int type, long notebookId) {
        WhereCondition whereCondition=NoteContentDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=NoteContentDao.Properties.NotebookId.eq(notebookId);
        List<NoteContent> list = noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        return list;
    }

    public void deleteNote(NoteContent noteContent){
        noteDao.delete(noteContent);
    }

    public void deleteType(int type,long notebookId){
        WhereCondition whereCondition=NoteContentDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=NoteContentDao.Properties.NotebookId.eq(notebookId);
        List<NoteContent> list = noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        noteDao.deleteInTx(list);
    }

}
