package com.bll.lnkstudy.mvp.model.paper;

import java.io.Serializable;
import java.util.List;

/**
 * 学生考试
 */
public class ExamItem implements Serializable {
    public int id;
    public int type;//1测卷 2考卷
    public long time;
    public String name;
    public String examUrl;
    public int commonTypeId;
    public String subject;
    public List<String> paths;//本地图片路径
}
