package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkBookBeanDao;
import com.bll.lnkstudy.greendao.HomeworkBookCorrectBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class HomeworkBookCorrectDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkBookCorrectDaoManager mDbController;

    private HomeworkBookCorrectBeanDao dao;

    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkBookCorrectDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkBookCorrectBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkBookCorrectDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkBookCorrectDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkBookCorrectDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= HomeworkBookCorrectBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    public void insertOrReplace(HomeworkBookCorrectBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(HomeworkBookCorrectBean bean) {
        dao.insertOrReplace(bean);
        List<HomeworkBookCorrectBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<HomeworkBookCorrectBean> queryCorrectAll(int bookID) {
        WhereCondition whereCondition= HomeworkBookCorrectBeanDao.Properties.BookId.eq(bookID);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public HomeworkBookCorrectBean queryCorrectBean(int bookId,int page){
        WhereCondition whereCondition= HomeworkBookCorrectBeanDao.Properties.BookId.eq(bookId);
        WhereCondition whereCondition1= HomeworkBookCorrectBeanDao.Properties.Page.eq(page);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    public boolean isExist(int bookId,int page){
        return queryCorrectBean(bookId,page)!=null;
    }

    public void clear(){
        dao.deleteAll();
    }

    public void delete(int booId){
        dao.deleteInTx(queryCorrectAll(booId));
    }

    public void delete(HomeworkBookCorrectBean bean){
        dao.delete(bean);
    }

}
