package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

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
    public int type;
    public String imageUrl;
    public String bookName;//书名
    public String bookDesc;//描述
    public int price;//书的价格
    public int semester;//学期
    public String area;//地区
    public int grade; //年级
    @SerializedName("subjectName")
    public int subject;//课目
    public int version;  //版本
    public String supply ;  //官方
    @Unique
    @SerializedName("bodyUrl")
    public String downloadUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public String bookDrawPath;  //book书的手写路径
    public long time;//观看时间
    public int pageIndex;//当前页
    public String pageUrl;//当前页路径
    @Transient
    public String drawUrl;//云存储的手写下载地址

    @Generated(hash = 697657062)
    public HomeworkBookBean(Long id, long userId, int bookId, int type, String imageUrl,
            String bookName, String bookDesc, int price, int semester, String area, int grade,
            int subject, int version, String supply, String downloadUrl, String bookPath,
            String bookDrawPath, long time, int pageIndex, String pageUrl) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.type = type;
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.bookDesc = bookDesc;
        this.price = price;
        this.semester = semester;
        this.area = area;
        this.grade = grade;
        this.subject = subject;
        this.version = version;
        this.supply = supply;
        this.downloadUrl = downloadUrl;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.time = time;
        this.pageIndex = pageIndex;
        this.pageUrl = pageUrl;
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
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
    public int getVersion() {
        return this.version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getSupply() {
        return this.supply;
    }
    public void setSupply(String supply) {
        this.supply = supply;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
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
   
}
