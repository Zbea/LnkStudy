package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaintingDrawingBeanDao;
import com.bll.lnkstudy.greendao.PaintingTypeBeanDao;
import com.bll.lnkstudy.mvp.model.NotebookBean;
import com.bll.lnkstudy.mvp.model.PaintingDrawingBean;
import com.bll.lnkstudy.mvp.model.PaintingTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaintingTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static PaintingTypeDaoManager mDbController;


    private PaintingTypeBeanDao dao;

    private long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    private WhereCondition whereUser= PaintingTypeBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaintingTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getPaintingTypeBeanDao();
    }
    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaintingTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaintingTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingTypeDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaintingTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(PaintingTypeBean bean) {
        dao.insertOrReplace(bean);
        List<PaintingTypeBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<PaintingTypeBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public List<PaintingTypeBean> queryAllByType(int type) {
        WhereCondition whereCondition=PaintingTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).build().list();
    }

    public PaintingTypeBean queryAllByGrade(int type,int grade) {
        WhereCondition whereCondition=PaintingTypeBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=PaintingTypeBeanDao.Properties.Grade.eq(grade);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    public void deleteBean(PaintingTypeBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }

}
