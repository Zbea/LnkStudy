package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.Constants;
import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class BookGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static BookGreenDaoManager mDbController;

    private final BookBeanDao bookBeanDao;  //book表

    private static WhereCondition whereUser;


    /**
     * 构造初始化
     */
    public BookGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        bookBeanDao = mDaoSession.getBookBeanDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static BookGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (BookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BookGreenDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= BookBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(BookBean bean) {
        bookBeanDao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(BookBean bean) {
        bookBeanDao.insertOrReplace(bean);
        List<BookBean> queryList = bookBeanDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    //查询书籍
    public BookBean queryBookByID(int bookID) {
        WhereCondition whereCondition1= BookBeanDao.Properties.BookId.eq(bookID);
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition1).build().unique();
    }


    //查询所有书籍
    public List<BookBean> queryAllBook() {
        return bookBeanDao.queryBuilder().where(whereUser).orderDesc(BookBeanDao.Properties.Time).build().list();
    }

    /**
     * 获取半年以前的书籍
     * @return
     */
    public List<BookBean> queryAllByHalfYear(){
        long time=System.currentTimeMillis()- Constants.halfYear;
        WhereCondition whereCondition1= BookBeanDao.Properties.Time.le(time);
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    /**
     * 获取打开过的书籍
     * @param isLook
     * @return
     */
    public List<BookBean> queryAllBook(boolean isLook,int size) {
        WhereCondition whereCondition1=BookBeanDao.Properties.IsLook.eq(isLook);
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition1).orderDesc(BookBeanDao.Properties.Time).limit(size).build().list();
    }

    //根据类别 细分子类
    public List<BookBean> queryAllBook(String type) {
        WhereCondition whereCondition2=BookBeanDao.Properties.SubtypeStr.eq(type);
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
    }

    //根据类别 细分子类 分页处理
    public List<BookBean> queryAllBook(String type, int page, int pageSize) {
        WhereCondition whereCondition2=BookBeanDao.Properties.SubtypeStr.eq(type);
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    public List<BookBean> search(String name) {
        WhereCondition whereCondition2=BookBeanDao.Properties.BookName.like("%"+name+"%");
        return bookBeanDao.queryBuilder().where(whereUser,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
    }

    //删除书籍数据d对象
    public void deleteBook(BookBean book){
        bookBeanDao.delete(book);
    }

    public void clear(){
        bookBeanDao.deleteAll();
    }

}
