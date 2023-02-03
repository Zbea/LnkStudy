package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.BookBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

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

    private BookBeanDao bookBeanDao;  //book表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    WhereCondition whereUser= BookBeanDao.Properties.UserId.eq(userId);


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
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(BookBean bean) {
        bookBeanDao.insertOrReplace(bean);
    }


    //根据bookId 查询书籍
    public BookBean queryBookByBookID(int bookID) {
        WhereCondition whereCondition= BookBeanDao.Properties.BookId.eq(bookID);
        BookBean queryBook = bookBeanDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryBook;
    }


    //查询所有书籍
    public List<BookBean> queryAllBook(int category) {
        WhereCondition whereCondition=BookBeanDao.Properties.Category.notEq(category);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition).orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类
    public List<BookBean> queryAllBook(int category, String type) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(category);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类 分页处理
    public List<BookBean> queryAllBook(int category, String type, int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(category);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //查找已收藏书籍
    public List<BookBean> queryAllBook(boolean isCollect) {
        WhereCondition whereCondition1=BookBeanDao.Properties.IsCollect.eq(isCollect);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //查找课本 细分子类
    public List<BookBean> queryAllTextBook(int category, String textType) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(category);
        WhereCondition whereCondition2=BookBeanDao.Properties.TextBookType.eq(textType);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    public List<BookBean> queryAllTextBook(int category, String textType, int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(category);
        WhereCondition whereCondition2=BookBeanDao.Properties.TextBookType.eq(textType);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //查找课本 细分子类 根据科目查找书籍
    public BookBean queryTextBook(int category, String textType, String course) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(category);
        WhereCondition whereCondition2=BookBeanDao.Properties.TextBookType.eq(textType);
        WhereCondition whereCondition3=BookBeanDao.Properties.SubjectName.eq(course);
        BookBean book = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
        return book;
    }

    //删除书籍数据d对象
    public void deleteBook(BookBean book){
        bookBeanDao.delete(book);
    }

}
