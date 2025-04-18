package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteContentBeanDao;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.painting.PaintingBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaintingBeanDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaintingBeanDaoManager mDbController;
    private final PaintingBeanDao dao;
    private static WhereCondition whereUser;

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
        long userId = MethodManager.getAccountId();
        whereUser= PaintingBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaintingBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(PaintingBean bean) {
        dao.insertOrReplace(bean);
        List<PaintingBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public void deleteBean(PaintingBean bean){
        dao.delete(bean);
    }

    public PaintingBean queryBean(int id) {
        WhereCondition whereCondition= PaintingBeanDao.Properties.ContentId.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition)
                .build().unique();
    }

    /**
     * 获取壁纸
     * @return
     */
    public List<PaintingBean> queryWallpapers() {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
    }

    /**
     * 获取壁纸
     * @param page
     * @param pageSize
     * @return
     */
    public List<PaintingBean> queryWallpapers(int page, int pageSize) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(1);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    public List<PaintingBean> queryPaintings() {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        return dao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
    }

    public List<PaintingBean> searchPaintings(String title) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);
        WhereCondition whereCondition2= PaintingBeanDao.Properties.Title.like("%"+title+"%");
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
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
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    /**
     * 获取该分类所有书画
     * @param paintingType
     * @return
     */
    public List<PaintingBean> queryPaintings(int paintingType) {
        WhereCondition whereCondition1= PaintingBeanDao.Properties.Type.eq(2);;
        WhereCondition whereCondition4= PaintingBeanDao.Properties.PaintingType.eq(paintingType);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition4)
                .orderDesc(PaintingBeanDao.Properties.Date)
                .build().list();
    }

    /**
     * 删除所有书画
     */
    public void deletePaintings(){
        dao.deleteInTx(queryPaintings());
    }

    public void clear(){
        dao.deleteAll();
    }

}
