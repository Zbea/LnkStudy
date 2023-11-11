package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.greendao.PaperContentBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaperContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaperContentDaoManager mDbController;
    private final PaperContentBeanDao dao;
    private static WhereCondition whereUser;

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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= PaperContentBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaperContentBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(PaperContentBean bean) {
        dao.insertOrReplace(bean);
        List<PaperContentBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    //通过考卷id查询所有试卷
    public  List<PaperContentBean> queryByID(int contentId) {
        WhereCondition whereCondition= PaperContentBeanDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<PaperContentBean> queryAll( String course, int categoryId) {
        WhereCondition whereCondition1= PaperContentBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= PaperContentBeanDao.Properties.TypeId.eq(categoryId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public void delete(String course, int categoryId){
        List<PaperContentBean> paperContentBeans=queryAll(course,categoryId);
        dao.deleteInTx(paperContentBeans);
    }

    public void deleteBean(PaperContentBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
