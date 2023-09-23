package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkBookBean  {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    @Unique
    public int bookId;
    public String imageUrl;
    public String bookName;//书名
    public String bookDesc;//描述
    public int price;//书的价格
    public String subtypeStr;
    public int semester;//学期
    public String area;//地区
    public int grade; //年级
    public int subject;//课目
    public String version;  //版本
    public String supply ;  //官方
    public String bodyUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public String bookDrawPath;  //book书的手写路径
    public long downDate;//下载时间
    public int pageIndex;//当前页
    public String pageUrl;//当前页路径
    public boolean isCloud;
    public int cloudId;
    @Transient
    public String drawUrl;//云存储的手写下载地址
    @Generated(hash = 1219055239)
    public HomeworkBookBean(Long id, long userId, int bookId, String imageUrl, String bookName,
            String bookDesc, int price, String subtypeStr, int semester, String area, int grade,
            int subject, String version, String supply, String bodyUrl, String bookPath,
            String bookDrawPath, long downDate, int pageIndex, String pageUrl, boolean isCloud,
            int cloudId) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.bookDesc = bookDesc;
        this.price = price;
        this.subtypeStr = subtypeStr;
        this.semester = semester;
        this.area = area;
        this.grade = grade;
        this.subject = subject;
        this.version = version;
        this.supply = supply;
        this.bodyUrl = bodyUrl;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.downDate = downDate;
        this.pageIndex = pageIndex;
        this.pageUrl = pageUrl;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 1174222334)
    public HomeworkBookBean() {
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
    public int getBookId() {
        return this.bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBookName() {
        return this.bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getBookDesc() {
        return this.bookDesc;
    }
    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getSubtypeStr() {
        return this.subtypeStr;
    }
    public void setSubtypeStr(String subtypeStr) {
        this.subtypeStr = subtypeStr;
    }
    public int getSemester() {
        return this.semester;
    }
    public void setSemester(int semester) {
        this.semester = semester;
    }
    public String getArea() {
        return this.area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public int getSubject() {
        return this.subject;
    }
    public void setSubject(int subject) {
        this.subject = subject;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSupply() {
        return this.supply;
    }
    public void setSupply(String supply) {
        this.supply = supply;
    }
    public String getBodyUrl() {
        return this.bodyUrl;
    }
    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public String getBookDrawPath() {
        return this.bookDrawPath;
    }
    public void setBookDrawPath(String bookDrawPath) {
        this.bookDrawPath = bookDrawPath;
    }
    public long getDownDate() {
        return this.downDate;
    }
    public void setDownDate(long downDate) {
        this.downDate = downDate;
    }
    public int getPageIndex() {
        return this.pageIndex;
    }
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    public String getPageUrl() {
        return this.pageUrl;
    }
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    public boolean getIsCloud() {
        return this.isCloud;
    }
    public void setIsCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }
    public int getCloudId() {
        return this.cloudId;
    }
    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }
   
}
