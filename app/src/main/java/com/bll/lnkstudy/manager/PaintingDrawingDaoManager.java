package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.PaintingDrawingBeanDao;
import com.bll.lnkstudy.mvp.model.PaintingDrawingBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaintingDrawingDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static PaintingDrawingDaoManager mDbController;


    private PaintingDrawingBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaintingDrawingBeanDao.Properties.UserId.eq(userId);

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
        return mDbController;
    }

    public void insertOrReplace(PaintingDrawingBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<PaintingDrawingBean> queryAllByType(int type) {
        WhereCondition whereCondition=PaintingDrawingBeanDao.Properties.Type.eq(type);
        List<PaintingDrawingBean> queryList = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return queryList;
    }

    public void deleteBean(PaintingDrawingBean bean){
        dao.delete(bean);
    }


}
