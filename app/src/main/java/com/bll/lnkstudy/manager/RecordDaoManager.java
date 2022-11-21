package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.PaperContentDao;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class RecordDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static RecordDaoManager mDbController;


    private RecordBeanDao recordBeanDao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= RecordBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public RecordDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        recordBeanDao = mDaoSession.getRecordBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static RecordDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (RecordDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new RecordDaoManager();
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
        List<RecordBean> queryList = recordBeanDao.queryBuilder().where(whereUser,whereCondition)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
        return queryList;
    }

    public void deleteBean(RecordBean bean){
        recordBeanDao.delete(bean);
    }


}
