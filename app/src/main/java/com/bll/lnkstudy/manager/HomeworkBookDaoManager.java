package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkBookBeanDao;
import com.bll.lnkstudy.mvp.model.BookBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class HomeworkBookDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkBookDaoManager mDbController;

    private HomeworkBookBeanDao dao;

    private final long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    WhereCondition whereUser= HomeworkBookBeanDao.Properties.UserId.eq(userId);


    /**
     * 构造初始化
     */
    public HomeworkBookDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkBookBeanDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkBookDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkBookDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkBookDaoManager();
                }
            }
        }
        return mDbController;
    }


    public void insertOrReplaceBook(HomeworkBookBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(HomeworkBookBean bean) {
        dao.insertOrReplace(bean);
        List<HomeworkBookBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public HomeworkBookBean queryBookByID(int bookID) {
        WhereCondition whereCondition= HomeworkBookBeanDao.Properties.BookId.eq(bookID);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique();
    }

    public List<HomeworkBookBean> search(String name) {
        WhereCondition whereCondition2=BookBeanDao.Properties.BookName.like("%"+name+"%");
        return dao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
    }

    public boolean isExist(int bookId){
        return queryBookByID(bookId)!=null;
    }

    public void clear(){
        dao.deleteAll();
    }

    public void delete(HomeworkBookBean bean){
        dao.delete(bean);
    }

}
