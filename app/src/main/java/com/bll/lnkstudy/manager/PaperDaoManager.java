package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.PaperContentDao;
import com.bll.lnkstudy.greendao.PaperDao;
import com.bll.lnkstudy.mvp.model.Paper;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaperDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static PaperDaoManager mDbController;


    private PaperDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaperDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getPaperDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaperDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaperDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaperDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(Paper bean) {
        dao.insertOrReplace(bean);

    }

    public long getInsertId(){
        List<Paper> queryList = dao.queryBuilder().where(whereUser).build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public Paper queryByContentID(int id) {
        Paper item = dao.queryBuilder().where(whereUser,PaperDao.Properties.ContentId.eq(id)).build().unique();
        return item;
    }

    /**
     *
     * @param type //作业还是考卷
     * @param courseId //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<Paper> queryAll(int type,int courseId, int categoryId) {
        WhereCondition whereCondition1= PaperDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= PaperDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition3= PaperDao.Properties.CategoryId.eq(categoryId);
        List<Paper> queryList = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().list();
        return queryList;
    }

    public void deleteBean(Paper bean){
        dao.delete(bean);
    }


}
