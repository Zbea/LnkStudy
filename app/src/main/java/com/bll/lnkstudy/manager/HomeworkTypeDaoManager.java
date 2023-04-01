package com.bll.lnkstudy.manager;

import android.util.Log;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkTypeDaoManager mDbController;


    private HomeworkTypeBeanDao dao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkTypeBeanDao.Properties.StudentId.eq(userId);

    /**
     * 构造初始化
     */
    public HomeworkTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkTypeBeanDao(); //note表
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkTypeDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    /**
     * 查找作业本
     * @param course
     * @param isCreate false 老师创建 true 学生创建
     * @return
     */
    public List<HomeworkTypeBean> queryAllByCourse(String course, boolean isCreate) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCreate.eq(isCreate);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
    }

    public void deleteBean(HomeworkTypeBean bean){
        dao.delete(bean);
    }


}
