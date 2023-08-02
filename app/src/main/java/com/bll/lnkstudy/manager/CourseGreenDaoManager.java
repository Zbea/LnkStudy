package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.CourseBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.CourseBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;


public class CourseGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static CourseGreenDaoManager mDbController;


    private final CourseBeanDao courseDao;

    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public CourseGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        courseDao = mDaoSession.getCourseBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static CourseGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (CourseGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new CourseGreenDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= CourseBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加课程
    public void insertOrReplaceCourse(CourseBean bean) {
        courseDao.insertOrReplace(bean);
    }

    public void insertAll(List<CourseBean> lists){
        courseDao.insertOrReplaceInTx(lists);
    }

    //根据Id 查询
    public CourseBean queryID(int id) {
        WhereCondition whereCondition1= CourseBeanDao.Properties.ViewId.eq(id);
        return  courseDao.queryBuilder().where(whereUser,whereCondition1).build().unique();
    }

    //删除
    public void deleteCourse(CourseBean bean){
        courseDao.delete(bean);
    }

    //全部删除
    public void deleteAll(){
        courseDao.deleteAll();
    }

    public void clear(){
        courseDao.deleteAll();
    }

}
