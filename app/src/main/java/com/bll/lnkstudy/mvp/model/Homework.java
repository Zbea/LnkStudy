package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class Homework {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int index;//当前第几个作业（位置下标）
    public String bgResId;//作业背景样式id
    public int courseId;//科目id
    public int homeworkTypeId;//作业本分组id

    public String title;//作业标题
    public long startDate;//开始时间
    public long endDate;//结束时间
    public String path;//内容存储路径
    public int page;//页码
    public int count;//作业内容个数

    public int state;//0未提交1已提交2已批改
    public boolean isSave;//本次作业是否已经完成
    @Generated(hash = 1716091859)
    public Homework(Long id, long userId, int index, String bgResId, int courseId,
            int homeworkTypeId, String title, long startDate, long endDate,
            String path, int page, int count, int state, boolean isSave) {
        this.id = id;
        this.userId = userId;
        this.index = index;
        this.bgResId = bgResId;
        this.courseId = courseId;
        this.homeworkTypeId = homeworkTypeId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.path = path;
        this.page = page;
        this.count = count;
        this.state = state;
        this.isSave = isSave;
    }
    @Generated(hash = 1431997646)
    public Homework() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getBgResId() {
        return this.bgResId;
    }
    public void setBgResId(String bgResId) {
        this.bgResId = bgResId;
    }
    public int getCourseId() {
        return this.courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getStartDate() {
        return this.startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public long getEndDate() {
        return this.endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public boolean getIsSave() {
        return this.isSave;
    }
    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }


}
