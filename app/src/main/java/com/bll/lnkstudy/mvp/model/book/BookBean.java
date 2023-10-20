package com.bll.lnkstudy.mvp.model.book;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

/**
 * 书籍
 */
@Entity
public class BookBean {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int bookId;
    public int bookPlusId;
    public int category;//0课本1书架
    public int typeId;//0教材1古籍2自然科学3社会科学4思维科学5运动才艺6题卷本
    public String subtypeStr;//子分类
    public String imageUrl;
    public String bookName;//书名
    public String bookDesc;//描述
    public int price;//书的价格
    public int semester;//学期
    public String area;//地区
    public int grade; //年级
    public int subjectName;//课目
    public String version;  //版本
    public String supply ;  //官方
    @Unique
    @SerializedName("bodyUrl")
    public String downloadUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public String bookDrawPath;  //book书的手写路径
    public long downDate;//下载时间(用于书架上传)
    public long time;//观看时间
    public int pageIndex;//当前页
    public String pageUrl;//当前页路径
    public boolean isLook;//是否已经打开
    public int dateState;//0当前课本1往期课本
    public boolean isLock=false;//未锁
    public boolean isCloud;
    public int cloudId;
    @Transient
    public String drawUrl;//云存储的手写下载地址
    @Transient
    public int bookVersion;  //题卷本
    @Transient
    public int loadSate;//0未下载 1正下载 2已下载
    @Transient
    public int buyStatus;//1已购买

    @Generated(hash = 682466723)
    public BookBean(Long id, long userId, int bookId, int bookPlusId, int category, int typeId,
            String subtypeStr, String imageUrl, String bookName, String bookDesc, int price,
            int semester, String area, int grade, int subjectName, String version, String supply,
            String downloadUrl, String bookPath, String bookDrawPath, long downDate, long time,
            int pageIndex, String pageUrl, boolean isLook, int dateState, boolean isLock,
            boolean isCloud, int cloudId) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.bookPlusId = bookPlusId;
        this.category = category;
        this.typeId = typeId;
        this.subtypeStr = subtypeStr;
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.bookDesc = bookDesc;
        this.price = price;
        this.semester = semester;
        this.area = area;
        this.grade = grade;
        this.subjectName = subjectName;
        this.version = version;
        this.supply = supply;
        this.downloadUrl = downloadUrl;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.downDate = downDate;
        this.time = time;
        this.pageIndex = pageIndex;
        this.pageUrl = pageUrl;
        this.isLook = isLook;
        this.dateState = dateState;
        this.isLock = isLock;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 269018259)
    public BookBean() {
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
    public int getBookPlusId() {
        return this.bookPlusId;
    }
    public void setBookPlusId(int bookPlusId) {
        this.bookPlusId = bookPlusId;
    }
    public int getCategory() {
        return this.category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getSubtypeStr() {
        return this.subtypeStr;
    }
    public void setSubtypeStr(String subtypeStr) {
        this.subtypeStr = subtypeStr;
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
    public int getSubjectName() {
        return this.subjectName;
    }
    public void setSubjectName(int subjectName) {
        this.subjectName = subjectName;
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
    public long getDownDate() {
        return this.downDate;
    }
    public void setDownDate(long downDate) {
        this.downDate = downDate;
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
    public boolean getIsLook() {
        return this.isLook;
    }
    public void setIsLook(boolean isLook) {
        this.isLook = isLook;
    }
    public int getDateState() {
        return this.dateState;
    }
    public void setDateState(int dateState) {
        this.dateState = dateState;
    }
    public boolean getIsLock() {
        return this.isLock;
    }
    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
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
