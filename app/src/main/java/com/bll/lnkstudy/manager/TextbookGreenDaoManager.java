package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.R;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.TextbookBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.book.TextbookBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class TextbookGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static TextbookGreenDaoManager mDbController;

    private final TextbookBeanDao dao;  //book表

    private static WhereCondition whereUser;


    /**
     * 构造初始化
     */
    public TextbookGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getTextbookBeanDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static TextbookGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (TextbookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new TextbookGreenDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= TextbookBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(TextbookBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(TextbookBean bean) {
        dao.insertOrReplace(bean);
        List<TextbookBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public void insertOrReplaceBooks(List<TextbookBean> beans) {
        dao.insertOrReplaceInTx(beans);
    }

    public List<TextbookBean> search(String name) {
        WhereCondition whereCondition=TextbookBeanDao.Properties.BookName.like("%"+name+"%");
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(TextbookBeanDao.Properties.Time)
                .build().list();
    }

    //查询课本
    public TextbookBean queryTextBookByID(int bookID) {
        WhereCondition whereCondition1= TextbookBeanDao.Properties.BookId.eq(bookID);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().unique();
    }

    public List<TextbookBean> queryAllTextBookByGrade(int grade, int semester) {
        String typeStr=MyApplication.Companion.getMContext().getString(R.string.textbook_tab_my);
        WhereCondition whereCondition1=TextbookBeanDao.Properties.TypeStr.eq(typeStr);
        WhereCondition whereCondition2=TextbookBeanDao.Properties.Grade.notEq(grade);
        WhereCondition whereCondition3=TextbookBeanDao.Properties.Semester.notEq(semester);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(TextbookBeanDao.Properties.Time).build().list();
    }

    //查找课本 细分子类
    public List<TextbookBean> queryAllTextBook(String textType) {
        WhereCondition whereCondition1=TextbookBeanDao.Properties.TypeStr.eq(textType);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(TextbookBeanDao.Properties.Time).build().list();
    }

    public List<TextbookBean> queryAllTextBook(String textType, int page, int pageSize) {
        WhereCondition whereCondition1=TextbookBeanDao.Properties.TypeStr.eq(textType);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(TextbookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    /**
     * 获取课本是否加锁
     * @return
     */
    public List<TextbookBean> queryAllTextbook(String typeStr,boolean isLock) {
        WhereCondition whereCondition1=TextbookBeanDao.Properties.TypeStr.eq(typeStr);
        WhereCondition whereCondition2=TextbookBeanDao.Properties.IsLock.eq(isLock);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(TextbookBeanDao.Properties.Time)
                .build().list();
    }

    //删除书籍数据d对象
    public void deleteBook(TextbookBean book){
        dao.delete(book);
    }


    public void deleteBooks(List<TextbookBean> bookBeans){
        dao.deleteInTx(bookBeans);
    }

    public void clear(){
        dao.deleteAll();
    }

}
