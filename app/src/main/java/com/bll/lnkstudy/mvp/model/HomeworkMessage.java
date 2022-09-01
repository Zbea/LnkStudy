package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class HomeworkMessage implements Serializable {

    public int id;
    public String title;
    public long date;
    public long endDate;
    public int state;
    public String course;
    public int courseId;
    public int homeworkTypeId;
    public String[] images = {
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
            , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
    };

}
