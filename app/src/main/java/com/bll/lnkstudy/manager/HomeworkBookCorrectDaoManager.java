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

    public List<HomeworkBookCorrectBean> queryCorrectBeanID(int bookID) {
        WhereCondition whereCondition= HomeworkBookCorrectBeanDao.Properties.BookId.eq(bookID);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public HomeworkBookCorrectBean queryCorrectBean(int bookId,String page){
        HomeworkBookCorrectBean correctBean = null;
        List<HomeworkBookCorrectBean> list=queryCorrectBeanID(bookId);
        for (HomeworkBookCorrectBean item: list) {
            String[] pages=item.pages.split(",");
            for (String pageStr:pages) {
                if (Objects.equals(pageStr, page)){
                    correctBean=item;
                }
            }
        }
        return correctBean;
    }

    /**
     * 判断当前页是否存在批改
     * @param bookId
     * @param page
     * @return
     */
    public boolean isExistCorrect(int bookId,String page){
        boolean isExist=false;
        List<HomeworkBookCorrectBean> list=queryCorrectBeanID(bookId);
        for (HomeworkBookCorrectBean item: list) {
            String[] pages=item.pages.split(",");
            for (String pageStr:pages) {
                if (Objects.equals(pageStr, page)){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    public void clear(){
        dao.deleteAll();
    }

    public void delete(int booId){
        dao.deleteInTx(queryCorrectBeanID(booId));
    }

    public void delete(HomeworkBookCorrectBean bean){
        dao.delete(bean);
    }

}
