package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.CourseBeanDao;
import com.bll.lnkstudy.greendao.CourseListDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.CourseBean;

import java.util.List;


public class CourseGreenDaoManager {

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
    private static CourseGreenDaoManager mDbController;


    private CourseBeanDao courseDao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public CourseGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        courseDao = mDaoSession.getCourseBeanDao();
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
    public static CourseGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (CourseGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new CourseGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加课程
    public void insertOrReplaceCourse(CourseBean bean) {
        courseDao.insertOrReplace(bean);
    }

    public void insertAll(List<CourseBean> lists){
        for (int i = 0; i < lists.size(); i++) {
            courseDao.insertOrReplace(lists.get(i));
        }
    }

    //根据Id 查询
    public CourseBean queryID(int id) {
        return  courseDao.queryBuilder().where(CourseBeanDao.Properties.ViewId.eq(id)).build().unique();
    }

    //删除
    public void deleteCourse(CourseBean bean){
        courseDao.delete(bean);
    }

    //全部删除
    public void deleteAll(){
        courseDao.deleteAll();
    }


}
