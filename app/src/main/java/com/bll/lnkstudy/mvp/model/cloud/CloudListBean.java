package com.bll.lnkstudy.mvp.model.cloud;

public class CloudListBean {
    public int id;
    public int type;
    public int subType;
    public String subTypeStr;
    public int dynasty;
    public String dynastyStr;
    public long date;//上传时间
    public int grade;
    public int year;
    public int bookId;//书籍id
    public String downloadUrl;//上传的下载链接
    public String zipUrl;//原来的下载链接
    public String listJson;//封面列表json
    public String contentJson;//内容json
    public String contentSubtypeJson;//子内容json
}