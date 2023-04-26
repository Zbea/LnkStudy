package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.mvp.model.PaintingDrawingBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.PaintingBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaintingBeanDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static PaintingBeanDaoManager mDbController;


    private PaintingBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaintingBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaintingBeanDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getPaintingBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaintingBeanDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaintingBeanDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingBeanDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaintingBean bean) {
        dao.insertOrReplace(bean);
    }

    public void deleteBean(PaintingBean bean){
        dao.delete(bean);
    }

    public PaintingBean queryBean(int id) {
        WhereCondition whereCondition= PaintingBeanDao.Properties.ContentId.eq(id);
        PaintingBean list = dao.queryBuilder().where(whereUser,whereCondition)
                .build().unique();
        return list;
    }

    /**
     * 获取壁纸
     * @return
     */
    public List<PaintingBean> queryWallpapers() {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(1);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
        return list;
    }

    /**
     * 获取壁纸
     * @param page
     * @param pageSize
     * @return
     */
    public List<PaintingBean> queryWallpapers(int page, int pageSize) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(1);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return list;
    }
    public List<PaintingBean> queryPaintings() {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
        return list;
    }

    /**
     * 获取 该分类全部书画大小
     * @param time
     * @param paintingType
     * @return
     */
    public int queryPaintings(int time, int paintingType) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        WhereCondition whereCondition2= PaintingBeanDao.Properties.Time.eq(time);
        WhereCondition whereCondition3= PaintingBeanDao.Properties.PaintingType.eq(paintingType);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
        return list.size();
    }
    /**
     * 获取书画
     * @param time 年代类型
     * @param paintingType 书画分类
     * @param page
     * @param pageSize
     * @return
     */
    public List<PaintingBean> queryPaintings(int time, int paintingType, int page, int pageSize) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        WhereCondition whereCondition2= PaintingBeanDao.Properties.Time.eq(time);
        WhereCondition whereCondition3= PaintingBeanDao.Properties.PaintingType.eq(paintingType);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return list;
    }

    /**
     * 获取 该分类全部书画大小
     * @param paintingType
     * @return
     */
    public int queryPaintings(int paintingType) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        WhereCondition whereCondition3= PaintingBeanDao.Properties.PaintingType.eq(paintingType);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition3)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
        return list.size();
    }

    /**
     * 获取素描、硬笔画
     * @param paintingType 书画分类
     * @param page
     * @param pageSize
     * @return
     */
    public List<PaintingBean> queryPaintings(int paintingType, int page, int pageSize) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        WhereCondition whereCondition3= PaintingBeanDao.Properties.PaintingType.eq(paintingType);
        List<PaintingBean> list = dao.queryBuilder().where(whereUser,whereCondition1,whereCondition3)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return list;
    }

    /**
     * 删除所有书画
     */
    public void deletePaintings(){
        dao.deleteInTx(queryPaintings());
    }


}
