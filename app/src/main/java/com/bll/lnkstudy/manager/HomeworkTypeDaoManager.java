package com.bll.lnkstudy.manager;

import com.bll.lnkstudy.MyApplication;
import com.bll.lnkstudy.greendao.DaoSession;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static HomeworkTypeDaoManager mDbController;


    private HomeworkTypeBeanDao dao;  //note表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= HomeworkTypeBeanDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public HomeworkTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkTypeBeanDao(); //note表
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkTypeDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(HomeworkTypeBean bean) {
        dao.insertOrReplace(bean);
    }


    /**
     * 将老师创建作业本保存在本地
     * @param course
     * @param lists
     */
    public void insertOrReplaceAll(String course,List<HomeworkTypeBean> lists){
        List<HomeworkTypeBean> homeworkTypeList=queryAllByCourse(course,false);
        for(HomeworkTypeBean item:lists){
            boolean isExist=false;
            for (HomeworkTypeBean ite:homeworkTypeList) {
                if (item.typeId==ite.typeId){
                    isExist=true;
                    break;
                }
            }
            if (!isExist)
                insertOrReplace(item);
        }

    }

    /**
     * 查找作业本
     * @param course
     * @param isCreate false 老师创建 true 学生创建
     * @return
     */
    public List<HomeworkTypeBean> queryAllByCourse(String course, boolean isCreate) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        WhereCondition whereCondition1=HomeworkTypeBeanDao.Properties.IsCreate.eq(isCreate);
        List<HomeworkTypeBean> lists = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        return lists;
    }

    public List<HomeworkTypeBean> queryAllByCourse(String course) {
        WhereCondition whereCondition=HomeworkTypeBeanDao.Properties.Course.eq(course);
        List<HomeworkTypeBean> lists = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return lists;
    }


    public void deleteBean(HomeworkTypeBean bean){
        dao.delete(bean);
    }


}
