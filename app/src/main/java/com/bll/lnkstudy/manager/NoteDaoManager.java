package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.DataBeanManager;
import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteDao;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class NoteDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static NoteDaoManager mDbController;
    private final NoteDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public NoteDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNoteDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= NoteDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(Note bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(Note bean) {
        dao.insertOrReplace(bean);
        List<Note> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public Note queryBean(long id) {
        WhereCondition whereCondition=NoteDao.Properties.Id.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NoteDao.Properties.Date).build().unique();
    }

    /**
     * 是否存在笔记
     * @return
     */
    public Boolean isExist(String typeStr,String title){
        WhereCondition whereCondition1=NoteDao.Properties.TypeStr.eq(typeStr);
        WhereCondition whereCondition2= NoteDao.Properties.Title.eq(title);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).unique()!=null;
    }

    /**
     * 是否存在笔记（云书库）
     * @return
     */
    public Boolean isExistCloud(String typeStr,String title){
        WhereCondition whereCondition1=NoteDao.Properties.TypeStr.eq(typeStr);
        WhereCondition whereCondition2= NoteDao.Properties.Title.eq(title);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).unique()!=null;
    }

    /**
     * 查询所有笔记
     * @return
     */
    public List<Note> queryNotes() {
        return dao.queryBuilder().where(whereUser).orderDesc(NoteDao.Properties.Date).build().list();
    }

    public List<Note> queryAll(String type) {
        WhereCondition whereCondition=NoteDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NoteDao.Properties.Date).build().list();
    }

    public Note queryBean(String type,String name) {
        WhereCondition whereCondition=NoteDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteDao.Properties.Title.eq(name);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    /**
     * @return
     */
    public List<Note> queryAll(String type, int page, int pageSize) {
        WhereCondition whereCondition=NoteDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NoteDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public void deleteBean(Note bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
