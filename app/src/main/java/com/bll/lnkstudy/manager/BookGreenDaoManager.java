package com.bll.lnkstudy.manager;


import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.mvp.model.BookBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperContentBean;
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

    public long insertOrReplaceGetId(BookBean bean) {
        bookBeanDao.insertOrReplace(bean);
        List<BookBean> queryList = bookBeanDao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public void insertOrReplaceBooks(List<BookBean> beans) {
        bookBeanDao.insertOrReplaceInTx(beans);
    }


    //根据bookId 查询书籍
    public BookBean queryTextBookByID(int bookID) {
        WhereCondition whereCondition= BookBeanDao.Properties.BookId.eq(bookID);
        BookBean queryBook = bookBeanDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryBook;
    }

    //根据bookId 查询书籍
    public BookBean queryBookByID(int bookID) {
        WhereCondition whereCondition= BookBeanDao.Properties.BookPlusId.eq(bookID);
        BookBean queryBook = bookBeanDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryBook;
    }


    //查询所有书籍
    public List<BookBean> queryAllBook() {
        WhereCondition whereCondition=BookBeanDao.Properties.Category.notEq(0);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition).orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类
    public List<BookBean> queryAllBook(String type) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类 分页处理
    public List<BookBean> queryAllBook(String type, int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //根据名称搜索 分页
    public List<BookBean> queryAllName(String name,String type, int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookName.like(name);
        WhereCondition whereCondition3=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }
    //根据名称搜素全部
    public List<BookBean> queryAllName(String name,String type) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.BookName.like(name);
        WhereCondition whereCondition3=BookBeanDao.Properties.BookType.eq(type);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
        return queryBookList;
    }

    //查找已收藏书籍
    public List<BookBean> queryAllBook(boolean isCollect) {
        WhereCondition whereCondition=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition1=BookBeanDao.Properties.IsCollect.eq(isCollect);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    //查找已收藏书籍
    public List<BookBean> queryAllBook(boolean isCollect, int page, int pageSize) {
        WhereCondition whereCondition=BookBeanDao.Properties.Category.notEq(0);
        WhereCondition whereCondition1=BookBeanDao.Properties.IsCollect.eq(isCollect);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition,whereCondition1)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
        return queryBookList;
    }

    //查找课本 细分子类
    public List<BookBean> queryAllTextBook(String textType) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.TextBookType.eq(textType);
        WhereCondition whereCondition3=BookBeanDao.Properties.DateState.eq(0);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookBeanDao.Properties.Time).build().list();
        return queryBookList;
    }

    public List<BookBean> queryAllTextBook( String textType, int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.TextBookType.eq(textType);
        WhereCondition whereCondition3=BookBeanDao.Properties.DateState.eq(0);//没有过期
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    /**
     * 获取所有教材除开往期教材
     * @return
     */
    public List<BookBean> queryAllTextBookOther() {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.DateState.notEq(1);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
        return queryBookList;
    }

    /**
     * 获取往期教材(未加锁的)
     * @return
     */
    public List<BookBean> queryAllTextBookOldUnlock() {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.DateState.eq(1);
        WhereCondition whereCondition3=BookBeanDao.Properties.IsLock.eq(false);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
        return queryBookList;
    }

    /**
     * 获取往期教材
     * @return
     */
    public List<BookBean> queryAllTextBookOld() {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.DateState.eq(1);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .build().list();
        return queryBookList;
    }

    /**
     * 获取往期教材
     * @return
     */
    public List<BookBean> queryAllTextBookOld( int page, int pageSize) {
        WhereCondition whereCondition1=BookBeanDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookBeanDao.Properties.DateState.eq(1);
        List<BookBean> queryBookList = bookBeanDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //删除书籍数据d对象
    public void deleteBook(BookBean book){
        bookBeanDao.delete(book);
    }


    public void deleteBooks(List<BookBean> bookBeans){
        bookBeanDao.deleteInTx(bookBeans);
    }

    public void clear(){
        bookBeanDao.deleteAll();
    }

}
