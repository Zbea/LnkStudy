package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeDao;
import com.bll.lnkstudy.mvp.model.HomeworkType;

import java.util.List;

public class HomeworkTypeDaoManager {


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
    private static HomeworkTypeDaoManager mDbController;


    private HomeworkTypeDao homeWorkTypeDao;  //note表

    /**
     * 构造初始化
     *
     * @param context
     */
    public HomeworkTypeDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        homeWorkTypeDao = mDaoSession.getHomeworkTypeDao(); //note表
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
    public static HomeworkTypeDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (HomeworkTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkTypeDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkType bean) {
        homeWorkTypeDao.insertOrReplace(bean);
    }

    public HomeworkType queryByID(Long noteID) {
        HomeworkType queryNote = homeWorkTypeDao.queryBuilder().where(HomeworkTypeDao.Properties.Id.eq(noteID)).build().unique();
        return queryNote;
    }

    public List<HomeworkType> queryAll() {
        List<HomeworkType> queryList = homeWorkTypeDao.queryBuilder().build().list();
        return queryList;
    }

    public List<HomeworkType> queryAllByCourseId(int courseId) {
        List<HomeworkType> queryList = homeWorkTypeDao.queryBuilder().where(HomeworkTypeDao.Properties.CourseId.eq(courseId)).build().list();
        return queryList;
    }


    public void deleteBean(HomeworkType bean){
        homeWorkTypeDao.delete(bean);
    }


}
