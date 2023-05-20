package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkPaperContentBeanDao;
import com.bll.lnkstudy.greendao.PaperContentBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperContentBean;
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkPaperContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static HomeworkPaperContentDaoManager mDbController;


    private HomeworkPaperContentBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkPaperContentBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public HomeworkPaperContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkPaperContentBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkPaperContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkPaperContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkPaperContentDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkPaperContentBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(HomeworkPaperContentBean bean) {
        dao.insertOrReplace(bean);
        List<HomeworkPaperContentBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    //通过考卷id查询所有试卷
    public List<HomeworkPaperContentBean> queryByID(int contentId) {
        WhereCondition whereCondition= HomeworkPaperContentBeanDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    /**
     *
     * @param course //科目id
     * @param categoryId //分组id
     * @return
     */
    public List<HomeworkPaperContentBean> queryAll(String course, int categoryId) {
        WhereCondition whereCondition1= HomeworkPaperContentBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition2= HomeworkPaperContentBeanDao.Properties.TypeId.eq(categoryId);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }


    public void deleteBean(HomeworkPaperContentBean bean){
        dao.delete(bean);
    }


    public void clear(){
        dao.deleteAll();
    }

}
