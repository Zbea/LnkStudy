package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkDetailsBeanDao;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class HomeworkDetailsDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkDetailsDaoManager mDbController;
    private final HomeworkDetailsBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkDetailsDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkDetailsBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkDetailsDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkDetailsDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkDetailsDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= HomeworkDetailsBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkDetailsBean bean) {
        List<HomeworkDetailsBean> beans=queryAllByType(bean.type);
        if (beans.size()>=10){
            List<HomeworkDetailsBean> detailsBeans=beans.subList(9,beans.size());
            dao.deleteInTx(detailsBeans);
        }
        dao.insertOrReplace(bean);
    }

    public List<HomeworkDetailsBean> queryAllByType(int type){
        WhereCondition whereCondition1= HomeworkDetailsBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition1).orderDesc(HomeworkDetailsBeanDao.Properties.Time).build().list();
    }

    /**
     * 获取批改详情
     * @return
     */
    public List<HomeworkDetailsBean> queryCorrect(){
        WhereCondition whereCondition1= HomeworkDetailsBeanDao.Properties.Type.eq(2);
        return dao.queryBuilder().where(whereUser,whereCondition1).orderDesc(HomeworkDetailsBeanDao.Properties.Time).limit(7).build().list();
    }

    public void clear(){
        dao.deleteAll();
    }

}