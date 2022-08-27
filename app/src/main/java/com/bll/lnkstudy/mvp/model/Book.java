package com.bll.lnkstudy.mvp.model;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 书籍
 */
@Entity
public class Book {
    @Id
    @Unique
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String assetUrl;//图片url
    public String downloadUrl;//书籍下载url
    public String description;//简介
    public String name;//书名
    public int price;//书的价格
    public int status;  // 1 可购买; 2 已购买; 3 已下载
    public String bookPath;  //book书的路径
    public int loadState =3;//1 已下载 ;2，正在下载;3 下载课本(课本名称)
    public String type ="0";  //书城六个类别
    public int textBook=0;//课本分类
    public int bookType=0;//书架所有分类
    public String province=""; //地区分类
    @SerializedName("class")
    public String classX ="0";  //科目
    public String grade ="0"; //年级
    public String version ="0";  //版本
    public Long time;//观看时间
    public int pageIndex=1;//当前页
    public String pageUpUrl;//上一页路径
    public String pageUrl;//当前页路径
    public boolean isCollect=false;//是否收藏

    @Generated(hash = 236105516)
    public Book(Long id, long userId, String assetUrl, String downloadUrl,
            String description, String name, int price, int status, String bookPath,
            int loadState, String type, int textBook, int bookType, String province,
            String classX, String grade, String version, Long time, int pageIndex,
            String pageUpUrl, String pageUrl, boolean isCollect) {
        this.id = id;
        this.userId = userId;
        this.assetUrl = assetUrl;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.name = name;
        this.price = price;
        this.status = status;
        this.bookPath = bookPath;
        this.loadState = loadState;
        this.type = type;
        this.textBook = textBook;
        this.bookType = bookType;
        this.province = province;
        this.classX = classX;
        this.grade = grade;
        this.version = version;
        this.time = time;
        this.pageIndex = pageIndex;
        this.pageUpUrl = pageUpUrl;
        this.pageUrl = pageUrl;
        this.isCollect = isCollect;
    }

    @Generated(hash = 1839243756)
    public Book() {
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
    public String getAssetUrl() {
        return this.assetUrl;
    }
    public void setAssetUrl(String assetUrl) {
        this.assetUrl = assetUrl;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public int getLoadState() {
        return this.loadState;
    }
    public void setLoadState(int loadState) {
        this.loadState = loadState;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getTextBook() {
        return this.textBook;
    }
    public void setTextBook(int textBook) {
        this.textBook = textBook;
    }
    public int getBookType() {
        return this.bookType;
    }
    public void setBookType(int bookType) {
        this.bookType = bookType;
    }
    public String getProvince() {
        return this.province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getClassX() {
        return this.classX;
    }
    public void setClassX(String classX) {
        this.classX = classX;
    }
    public String getGrade() {
        return this.grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
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
    public boolean getIsCollect() {
        return this.isCollect;
    }
    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public String getPageUpUrl() {
        return this.pageUpUrl;
    }

    public void setPageUpUrl(String pageUpUrl) {
        this.pageUpUrl = pageUpUrl;
    }
}
