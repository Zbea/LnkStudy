package com.bll.lnkstudy.mvp.model.book;

import com.bll.lnkstudy.MethodManager;
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
 * 教材
 */
@Entity
public class TextbookBean {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int bookId;
    public int type;
    public String typeStr;
    public String imageUrl;
    public String bookName;//书名
    public int price;//书的价格
    public int semester;//学期
    public String area;//地区
    public int grade; //年级
    @SerializedName("subjectName")
    public int subject;//课目
    @SerializedName("bodyUrl")
    public String downloadUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public String bookDrawPath;  //book书的手写路径
    public long time;//观看时间
    public int pageIndex;//当前页
    public String pageUrl;//当前页路径
    public boolean isLock;//未锁
    @Transient
    public String bookDesc;//描述
    @Transient
    public int version;  //版本
    @Transient
    public String drawUrl;//云存储的手写下载地址
    @Transient
    public int loadSate;//0未下载 1正下载 2已下载
    @Transient
    public int buyStatus;//1已购买
    @Transient
    public int cloudId;
    @Generated(hash = 1817654868)
    public TextbookBean(Long id, long userId, int bookId, int type, String typeStr, String imageUrl,
            String bookName, int price, int semester, String area, int grade, int subject,
            String downloadUrl, String bookPath, String bookDrawPath, long time, int pageIndex,
            String pageUrl, boolean isLock) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.type = type;
        this.typeStr = typeStr;
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.price = price;
        this.semester = semester;
        this.area = area;
        this.grade = grade;
        this.subject = subject;
        this.downloadUrl = downloadUrl;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.time = time;
        this.pageIndex = pageIndex;
        this.pageUrl = pageUrl;
        this.isLock = isLock;
    }
    @Generated(hash = 952130907)
    public TextbookBean() {
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
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
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
    public boolean getIsLock() {
        return this.isLock;
    }
    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

}
