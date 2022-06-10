package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.bll.lnkstudy.greendao.BookDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.DateDayEventDao;
import com.bll.lnkstudy.greendao.DatePlanEventDao;
import com.bll.lnkstudy.mvp.model.Book;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class BookGreenDaoManager {


    /**
     * 数据库名字
     */
    private String DB_NAME = "plan.db";  //数据库名字
    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private static BookGreenDaoManager mDbController;


    private BookDao bookDao;  //book表

    /**
     * 构造初始化
     *
     * @param context
     */
    public BookGreenDaoManager(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

        bookDao = mDaoSession.getBookDao(); //book表
    }


    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        }
        db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static BookGreenDaoManager getInstance(Context context) {
        if (mDbController == null) {
            synchronized (BookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BookGreenDaoManager(context);
                }
            }
        }
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(Book bean) {
        bookDao.insertOrReplace(bean);
    }


    //根据bookId 查询书籍
    public Book queryBookByBookID(Long bookID) {
        Book queryBook = bookDao.queryBuilder().where(BookDao.Properties.Id.eq(bookID)).build().unique();
        return queryBook;
    }


    //查询所有书籍 根据类别
    public List<Book> queryAllBook(String type) {
        WhereCondition whereCondition=BookDao.Properties.Type.eq(type);//根据类别查询所有书籍
        List<Book> queryBookList = bookDao.queryBuilder().where(whereCondition).orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类
    public List<Book> queryAllBook(String type,String flag) {
        WhereCondition whereCondition=BookDao.Properties.Type.eq(""+type);//根据类别查询所有书籍
        WhereCondition whereCondition1=BookDao.Properties.Grade.eq(""+flag);//根据类别查询所有书籍
        List<Book> queryBookList = bookDao.queryBuilder().where(whereCondition,whereCondition1).orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //查找已收藏书籍
    public List<Book> queryAllBook(String type,boolean isCollect) {
        WhereCondition whereCondition=BookDao.Properties.Type.eq(""+type);//根据类别查询所有书籍
        WhereCondition whereCondition1=BookDao.Properties.IsCollect.eq(isCollect);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereCondition,whereCondition1).orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //查找课本 细分子类
    public List<Book> queryAllTextBook(String type,int flag) {
        WhereCondition whereCondition=BookDao.Properties.Type.eq(""+type);//根据类别查询所有书籍
        WhereCondition whereCondition1=BookDao.Properties.BookType.eq(""+flag);//根据类别查询所有书籍
        List<Book> queryBookList = bookDao.queryBuilder().where(whereCondition,whereCondition1).orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }


    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

}
