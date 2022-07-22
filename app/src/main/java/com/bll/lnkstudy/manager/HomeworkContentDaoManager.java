package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkContentDao;
import com.bll.lnkstudy.greendao.HomeworkDao;
import com.bll.lnkstudy.greendao.HomeworkTypeDao;
import com.bll.lnkstudy.mvp.model.HomeworkContent;
import com.bll.lnkstudy.mvp.model.HomeworkType;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkContentDaoManager {


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
    private static HomeworkContentDaoManager mDbController;


    private HomeworkContentDao homeworkContentDao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public HomeworkContentDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        homeworkContentDao = mDaoSession.getHomeworkContentDao();
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
    public static HomeworkContentDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (HomeworkContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkContentDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkContent bean) {
        homeworkContentDao.insertOrReplace(bean);
    }

    public  List<HomeworkContent> queryByID(Long homeworkID) {
        List<HomeworkContent> querys = homeworkContentDao.queryBuilder()
                .where(HomeworkContentDao.Properties.HomeworkId.eq(homeworkID)).build().list();
        return querys;
    }


    public List<HomeworkContent> queryAllByType(int courseId,int homeworkTypeId) {

        WhereCondition whereCondition= HomeworkContentDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition1=HomeworkContentDao.Properties.HomeworkTypeId.eq(homeworkTypeId);

        List<HomeworkContent> queryList = homeworkContentDao.queryBuilder().where(whereCondition,whereCondition1).build().list();
//                .orderDesc(HomeworkContentDao.Properties.Date).build().list();
        return queryList;
    }


    public void deleteBean(HomeworkContent bean){
        homeworkContentDao.delete(bean);
    }


}
