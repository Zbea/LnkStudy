package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteDao;
import com.bll.lnkstudy.mvp.model.Note;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NoteGreenDaoManager {


    /**
     * 数据库名字
     */
    private String DB_NAME = "plan.db";  //数据库名字
    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private static NoteGreenDaoManager mDbController;


    private NoteDao noteDao;  //note表

    /**
     * 构造初始化
     *
     * @param context
     */
    public NoteGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        noteDao = mDaoSession.getNoteDao(); //note表
    }


    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        }
        db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (NoteGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplaceNote(Note bean) {
        noteDao.insertOrReplace(bean);
    }

    public Note queryByNoteID(Long noteID) {
        Note queryNote = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(noteID)).build().unique();
        return queryNote;
    }

    public List<Note> queryAllNote() {
        List<Note> queryNoteList = noteDao.queryBuilder().build().list();
        return queryNoteList;
    }

    public List<Note> queryAllNote(int type) {
        if (type==0){
            return noteDao.queryBuilder().orderDesc(NoteDao.Properties.Id).build().list();
        }
        WhereCondition whereCondition=NoteDao.Properties.Type.eq(type);
        List<Note> querynoteList = noteDao.queryBuilder().where(whereCondition).orderDesc(NoteDao.Properties.Id).build().list();
        return querynoteList;
    }

    public void deleteNote(Note note){
        noteDao.delete(note);
    }

    public void deleteType(int type){
        WhereCondition whereCondition=NoteDao.Properties.Type.eq(type);
        List<Note> queryNoteList = noteDao.queryBuilder().where(whereCondition).build().list();
        noteDao.deleteInTx(queryNoteList);
    }

}
