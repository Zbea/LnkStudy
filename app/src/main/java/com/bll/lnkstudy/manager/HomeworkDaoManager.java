package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkDao;
import com.bll.lnkstudy.greendao.HomeworkTypeDao;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.Homework;
import com.bll.lnkstudy.mvp.model.HomeworkType;
import com.bll.lnkstudy.mvp.model.Note;
import com.bll.lnkstudy.mvp.model.RecordBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkDaoManager {
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
    private static HomeworkDaoManager mDbController;


    private HomeworkDao homeworkDao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public HomeworkDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        homeworkDao = mDaoSession.getHomeworkDao();
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
    public static HomeworkDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (HomeworkDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(Homework bean) {
        homeworkDao.insertOrReplace(bean);

    }


    public long getInsertId(){
        List<Homework> queryList = homeworkDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public Homework queryByID(Long id) {
        Homework homework = homeworkDao.queryBuilder().where(HomeworkDao.Properties.Id.eq(id)).build().unique();
        return homework;
    }

    public List<Homework> queryAllByType(int courseId,int homeworkTypeId) {
        WhereCondition whereCondition=HomeworkDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition1=HomeworkDao.Properties.HomeworkTypeId.eq(homeworkTypeId);
        List<Homework> queryList = homeworkDao.queryBuilder().where(whereCondition,whereCondition1).build().list();
//                .orderDesc(HomeworkDao.Properties.StartDate).build().list();
        return queryList;
    }

    public void deleteBean(Homework bean){
        homeworkDao.delete(bean);
    }


}
