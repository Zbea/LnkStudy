package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaintingDrawingBeanDao;
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class PaintingDrawingDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static PaintingDrawingDaoManager mDbController;
    private final PaintingDrawingBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public PaintingDrawingDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaintingDrawingBeanDao();
    }
    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaintingDrawingDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaintingDrawingDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingDrawingDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= PaintingDrawingBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(PaintingDrawingBean bean) {
        dao.insertOrReplace(bean);
    }

    public long insertOrReplaceGetId(PaintingDrawingBean bean) {
        dao.insertOrReplace(bean);
        List<PaintingDrawingBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<PaintingDrawingBean> queryAllByType(int type,int cloudId) {
        WhereCondition whereCondition=PaintingDrawingBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition1=PaintingDrawingBeanDao.Properties.CloudId.eq(cloudId);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderAsc(PaintingDrawingBeanDao.Properties.Date).build().list();
    }

    public void deleteBean(PaintingDrawingBean bean){
        dao.delete(bean);
    }

    public void deleteBean(int type,int cloudId){
        List<PaintingDrawingBean> list=queryAllByType(type, cloudId);
        dao.deleteInTx(list);
    }

    public void clear(){
        dao.deleteAll();
    }
}
