package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.greendao.PaperTypeBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class PaperTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static PaperTypeDaoManager mDbController;


    private PaperTypeBeanDao dao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= PaperTypeBeanDao.Properties.StudentId.eq(userId);

    /**
     * 构造初始化
     */
    public PaperTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getPaperTypeBeanDao(); //note表
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static PaperTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (PaperTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new PaperTypeDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(PaperTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<PaperTypeBean> queryAll() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public void deleteBean(PaperTypeBean bean){
        dao.delete(bean);
    }


}
