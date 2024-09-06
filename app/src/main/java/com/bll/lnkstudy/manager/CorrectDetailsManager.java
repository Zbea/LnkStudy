package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.CorrectDetailsBeanDao;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.NoteDao;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.CorrectDetailsBean;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class CorrectDetailsManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static CorrectDetailsManager mDbController;

    private final CorrectDetailsBeanDao dao;

    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public CorrectDetailsManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getCorrectDetailsBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static CorrectDetailsManager getInstance() {
        if (mDbController == null) {
            synchronized (CorrectDetailsManager.class) {
                if (mDbController == null) {
                    mDbController = new CorrectDetailsManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= CorrectDetailsBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(CorrectDetailsBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<CorrectDetailsBean> queryList(String course,int type) {
        WhereCondition whereCondition=CorrectDetailsBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=CorrectDetailsBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderDesc(CorrectDetailsBeanDao.Properties.Date)
                .build().list();
    }


    public List<CorrectDetailsBean> queryList(String course,int type, int page, int pageSize) {
        WhereCondition whereCondition=CorrectDetailsBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=CorrectDetailsBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderDesc(CorrectDetailsBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }


    public void clear(){
        dao.deleteAll();
    }

}
