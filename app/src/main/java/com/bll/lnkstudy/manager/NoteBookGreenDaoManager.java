package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteBookDao;
import com.bll.lnkstudy.mvp.model.NoteBook;

import java.util.List;

public class NoteBookGreenDaoManager {

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
    private static NoteBookGreenDaoManager mDbController;


    private NoteBookDao noteDao;  //note表

    /**
     * 构造初始化
     *
     * @param context
     */
    public NoteBookGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        noteDao = mDaoSession.getNoteBookDao(); //note表
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
    public static NoteBookGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (NoteBookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteBookGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplaceNote(NoteBook bean) {
        noteDao.insertOrReplace(bean);
    }

    public NoteBook queryByNoteID(Long noteID) {
        NoteBook queryNote = noteDao.queryBuilder().where(NoteBookDao.Properties.Id.eq(noteID)).build().unique();
        return queryNote;
    }

    public List<NoteBook> queryAllNote() {
        List<NoteBook> queryNoteList = noteDao.queryBuilder().build().list();
        return queryNoteList;
    }


    public void deleteNote(NoteBook note){
        noteDao.delete(note);
    }

}
