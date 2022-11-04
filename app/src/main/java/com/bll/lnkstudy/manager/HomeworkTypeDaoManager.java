package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeDao;
import com.bll.lnkstudy.mvp.model.HomeworkType;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

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


    private HomeworkTypeDao dao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkTypeDao.Properties.UserId.eq(userId);

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
        dao = mDaoSession.getHomeworkTypeDao(); //note表
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
        dao.insertOrReplace(bean);
    }


    /**
     * 将老师创建作业本保存在本地
     * @param courseId
     * @param lists
     */
    public void insertOrReplaceAll(int courseId,List<HomeworkType> lists){
        List<HomeworkType> homeworkTypeList=queryAllByCourseId(courseId,false);
        for(HomeworkType item:lists){
            boolean isExist=false;
            for (HomeworkType ite:homeworkTypeList) {
                if (item.typeId==ite.typeId){
                    isExist=true;
                    break;
                }
            }
            if (!isExist)
                insertOrReplace(item);
        }

    }

    /**
     * 查找作业本
     * @param courseId
     * @param isCreate false 老师创建 true 学生创建
     * @return
     */
    public List<HomeworkType> queryAllByCourseId(int courseId,boolean isCreate) {
        WhereCondition whereCondition=HomeworkTypeDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition1=HomeworkTypeDao.Properties.IsCreate.eq(isCreate);
        List<HomeworkType> lists = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        return lists;
    }

    public List<HomeworkType> queryAllByCourseId(int courseId) {
        WhereCondition whereCondition=HomeworkTypeDao.Properties.CourseId.eq(courseId);
        List<HomeworkType> lists = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return lists;
    }


    public void deleteBean(HomeworkType bean){
        dao.delete(bean);
    }


}
