package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.mvp.model.PaperBean;
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


    private PaperBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaperBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getPaperBeanDao();
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

    public void insertOrReplace(PaperBean bean) {
        dao.insertOrReplace(bean);

    }

    public long getInsertId(){
        List<PaperBean> queryList = dao.queryBuilder().where(whereUser).build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public PaperBean queryByContentID(int id) {
        PaperBean item = dao.queryBuilder().where(whereUser,PaperBeanDao.Properties.ContentId.eq(id)).build().unique();
        return item;
    }

    /**
     *
     * @param type //作业还是考卷
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperBean> queryAll(int type, String course, int categoryId) {
        WhereCondition whereCondition1= PaperBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= PaperBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition3= PaperBeanDao.Properties.CategoryId.eq(categoryId);
        List<PaperBean> queryList = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().list();
        return queryList;
    }

    public void deleteBean(PaperBean bean){
        dao.delete(bean);
    }


}
