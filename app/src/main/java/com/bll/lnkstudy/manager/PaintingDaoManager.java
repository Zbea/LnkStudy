package com.bll.lnkstudy.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoMaster;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.GreenDaoUpgradeHelper;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.mvp.model.PaintingBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaintingDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    /**
     *
     */
    private static PaintingDaoManager mDbController;


    private PaintingBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaintingBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public PaintingDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        dao = mDaoSession.getPaintingBeanDao();
    }
    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaintingDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaintingDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaintingDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaintingBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<PaintingBean> queryAllByType(int type) {
        WhereCondition whereCondition=PaintingBeanDao.Properties.Type.eq(type);
        List<PaintingBean> queryList = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return queryList;
    }

    public void deleteBean(PaintingBean bean){
        dao.delete(bean);
    }


}
