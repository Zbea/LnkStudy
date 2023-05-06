package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.greendao.PaperContentBeanDao;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean;
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


    private PaperContentBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaperContentBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaperContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaperContentBeanDao();
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

    public void insertOrReplace(PaperContentBean bean) {
        dao.insertOrReplace(bean);
    }

    //通过考卷id查询所有试卷
    public  List<PaperContentBean> queryByID(int contentId) {
        WhereCondition whereCondition= PaperContentBeanDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     *
     * @param type //作业还是考卷
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperContentBean> queryAll(int type, String course, int categoryId) {
        WhereCondition whereCondition1= PaperContentBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= PaperContentBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition3= PaperContentBeanDao.Properties.CategoryId.eq(categoryId);
        List<PaperContentBean> queryList = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().list();
        return queryList;
    }


    public void deleteBean(PaperContentBean bean){
        dao.delete(bean);
    }

    public void deleteAllByType(int type){
        WhereCondition whereCondition1= PaperContentBeanDao.Properties.Type.eq(type);
        List<PaperContentBean> queryList = dao.queryBuilder().where(whereUser,whereCondition1).build().list();
        dao.deleteInTx(queryList);
    }

}
