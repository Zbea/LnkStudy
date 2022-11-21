package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.greendao.PaperContentDao;
import com.bll.lnkstudy.mvp.model.PaperContent;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaperContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static PaperContentDaoManager mDbController;


    private PaperContentDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaperContentDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaperContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaperContentDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaperContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaperContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaperContentDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaperContent bean) {
        dao.insertOrReplace(bean);
    }

    //通过考卷id查询所有试卷
    public  List<PaperContent> queryByID(int contentId) {
        WhereCondition whereCondition= PaperContentDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     *
     * @param type //作业还是考卷
     * @param courseId //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperContent> queryAll(int type,int courseId, int categoryId) {
        WhereCondition whereCondition1= PaperContentDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= PaperContentDao.Properties.CourseId.eq(courseId);
        WhereCondition whereCondition3= PaperContentDao.Properties.CategoryId.eq(categoryId);
        List<PaperContent> queryList = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().list();
        return queryList;
    }


    public void deleteBean(PaperContent bean){
        dao.delete(bean);
    }


}
