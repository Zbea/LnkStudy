package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaperDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaperDaoManager mDbController;
    private final PaperBeanDao dao;
    private static WhereCondition whereUser;

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
        long userId = MethodManager.getAccountId();
        whereUser= PaperBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaperBean bean) {
        dao.insertOrReplace(bean);

    }

    /**
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperBean> queryAll(String course, int categoryId) {
        WhereCondition whereCondition1= PaperBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= PaperBeanDao.Properties.PaperTypeId.eq(categoryId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }


    public List<PaperBean> search(String title) {
        WhereCondition whereCondition1= PaperBeanDao.Properties.Title.like("%"+title+"%");
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public void delete(String course, int categoryId){
        List<PaperBean> papers=queryAll(course,categoryId);
        dao.deleteInTx(papers);
    }

    public void deleteBean(PaperBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }


}
