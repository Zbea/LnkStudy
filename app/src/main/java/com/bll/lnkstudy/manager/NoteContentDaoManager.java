package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.DataBeanManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteContentBeanDao;
import com.bll.lnkstudy.mvp.model.NoteContentBean;
import com.bll.lnkstudy.mvp.model.NoteTypeBean;
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

    public long insertOrReplaceGetId(NoteContentBean bean) {
        noteDao.insertOrReplace(bean);
        List<NoteContentBean> queryList = noteDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public List<NoteContentBean> queryAll(String type, String notebookStr,int grade) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookTitle.eq(notebookStr);
        WhereCondition whereCondition2=NoteContentBeanDao.Properties.Grade.eq(grade);
        return noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2).build().list();
    }

    public List<NoteContentBean> search(String title) {
        WhereCondition whereCondition=NoteContentBeanDao.Properties.Title.like("%"+title+"%");
        return noteDao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public void deleteNote(NoteContentBean noteContent){
        noteDao.delete(noteContent);
    }

    public void deleteType(String type,String notebookStr,int grade){
        WhereCondition whereCondition=NoteContentBeanDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentBeanDao.Properties.NotebookTitle.eq(notebookStr);
        WhereCondition whereCondition2=NoteContentBeanDao.Properties.Grade.eq(grade);
        List<NoteContentBean> list = noteDao.queryBuilder().where(whereUser,whereCondition,whereCondition1,whereCondition2).build().list();
        noteDao.deleteInTx(list);
    }

    public void clear(){
        noteDao.deleteAll();
    }

}
