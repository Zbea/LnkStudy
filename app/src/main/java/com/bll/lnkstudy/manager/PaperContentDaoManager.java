package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaperContentDao;
import com.bll.lnkstudy.mvp.model.PaperContent;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaperContentDaoManager {


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
    private static PaperContentDaoManager mDbController;


    private PaperContentDao dao;

    /**
     * 构造初始化
     *
     * @param context
     */
    public PaperContentDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        dao = mDaoSession.getPaperContentDao();
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
    public static PaperContentDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (PaperContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaperContentDaoManager(context);
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaperContent bean) {
        dao.insertOrReplace(bean);
    }

    //通过考卷id查询所有试卷
    public  List<PaperContent> queryByID(int mUserId, long paperId) {
        WhereCondition whereCondition= PaperContentDao.Properties.UserId.eq(mUserId);
        WhereCondition whereCondition1= PaperContentDao.Properties.PaperId.eq(paperId);
        return dao.queryBuilder().where(whereCondition,whereCondition1).build().list();
    }

    /**
     *
     * @param mUserId 用户id
     * @param type //作业还是考卷
     * @param courseId //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperContent> queryAll(int mUserId, int type,int courseId, int categoryId) {
        WhereCondition whereCondition= PaperContentDao.Properties.UserId.eq(mUserId);
        WhereCondition whereCondition1= PaperContentDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= PaperContentDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition3= PaperContentDao.Properties.CategoryId.eq(categoryId);
        List<PaperContent> queryList = dao.queryBuilder().where(whereCondition,whereCondition1,whereCondition2,whereCondition3).build().list();
        return queryList;
    }


    public void deleteBean(PaperContent bean){
        dao.delete(bean);
    }


}
