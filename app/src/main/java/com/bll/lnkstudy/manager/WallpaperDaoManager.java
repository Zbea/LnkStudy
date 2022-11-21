package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.BookDao;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.greendao.WallpaperBeanDao;
import com.bll.lnkstudy.mvp.model.Book;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.WallpaperBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class WallpaperDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static WallpaperDaoManager mDbController;


    private WallpaperBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= WallpaperBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public WallpaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getWallpaperBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static WallpaperDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (WallpaperDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new WallpaperDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(WallpaperBean bean) {
        dao.insertOrReplace(bean);
    }

    public void deleteBean(WallpaperBean bean){
        dao.delete(bean);
    }

    /**
     * 获取壁纸
     * @param type 0
     * @return
     */
    public List<WallpaperBean> queryAll(int type) {
        WhereCondition whereCondition1= WallpaperBeanDao.Properties.Type.eq(type);
        List<WallpaperBean> list = dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(WallpaperBeanDao.Properties.Date)
                .build().list();
        return list;
    }

    /**
     * 获取壁纸
     * @param type 0
     * @param page
     * @param pageSize
     * @return
     */
    public List<WallpaperBean> queryAll(int type, int page, int pageSize) {
        WhereCondition whereCondition1= WallpaperBeanDao.Properties.Type.eq(type);
        List<WallpaperBean> list = dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(WallpaperBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return list;
    }

    /**
     * 获取 该分类全部书画大小
     * @param type
     * @param time
     * @param paintingType
     * @return
     */
    public int queryAllPainting(int type,int time,int paintingType) {
        WhereCondition whereCondition1= WallpaperBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= WallpaperBeanDao.Properties.Time.eq(time);
        WhereCondition whereCondition3= WallpaperBeanDao.Properties.PaintingType.eq(paintingType);
        List<WallpaperBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(WallpaperBeanDao.Properties.Date)
                .build().list();
        return list.size();
    }
    /**
     * 获取书画
     * @param type 1
     * @param time 年代类型
     * @param paintingType 书画分类
     * @param page
     * @param pageSize
     * @return
     */
    public List<WallpaperBean> queryAllPainting(int type,int time,int paintingType, int page, int pageSize) {
        WhereCondition whereCondition1= WallpaperBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= WallpaperBeanDao.Properties.Time.eq(time);
        WhereCondition whereCondition3= WallpaperBeanDao.Properties.PaintingType.eq(paintingType);
        List<WallpaperBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(WallpaperBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return list;
    }

}
