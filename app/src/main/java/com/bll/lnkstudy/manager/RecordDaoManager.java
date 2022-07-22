package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.RecordBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class RecordDaoManager {


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
    private static RecordDaoManager mDbController;


    private RecordBeanDao recordBeanDao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public RecordDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        recordBeanDao = mDaoSession.getRecordBeanDao();
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
    public static RecordDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (RecordDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new RecordDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(RecordBean bean) {
        recordBeanDao.insertOrReplace(bean);
    }


    public List<RecordBean> queryAllByCourseId(int courseId) {
        WhereCondition whereCondition=RecordBeanDao.Properties.CourseId.eq(courseId);
        List<RecordBean> queryList = recordBeanDao.queryBuilder().where(whereCondition).orderDesc(RecordBeanDao.Properties.Date).build().list();
        return queryList;
    }

    public void deleteBean(RecordBean bean){
        recordBeanDao.delete(bean);
    }


}
