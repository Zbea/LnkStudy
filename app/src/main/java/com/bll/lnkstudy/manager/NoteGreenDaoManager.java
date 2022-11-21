package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.HomeworkTypeDao;
import com.bll.lnkstudy.greendao.NoteDao;
import com.bll.lnkstudy.mvp.model.Note;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NoteGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static NoteGreenDaoManager mDbController;


    private NoteDao noteDao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NoteDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NoteGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        noteDao = mDaoSession.getNoteDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteGreenDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplaceNote(Note bean) {
        noteDao.insertOrReplace(bean);
    }

    public Note queryByNoteID(Long noteID) {
        WhereCondition whereCondition=NoteDao.Properties.Id.eq(noteID);
        Note queryNote = noteDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryNote;
    }

    /**
     * 查找全部笔记，按照自增长id倒序
     * @return
     */
    public List<Note> queryAll(){
        return noteDao.queryBuilder().where(whereUser).orderDesc(NoteDao.Properties.Id).build().list();
    }

    public List<Note> queryAllNote(int type) {
        WhereCondition whereCondition=NoteDao.Properties.Type.eq(type);
        List<Note> list = noteDao.queryBuilder().where(whereUser,whereCondition).orderDesc(NoteDao.Properties.NowDate).build().list();
        return list;
    }

    public void deleteNote(Note note){
        noteDao.delete(note);
    }

    public void deleteType(int type){
        WhereCondition whereCondition=NoteDao.Properties.Type.eq(type);
        List<Note> list = noteDao.queryBuilder().where(whereUser,whereCondition).build().list();
        noteDao.deleteInTx(list);
    }

}
