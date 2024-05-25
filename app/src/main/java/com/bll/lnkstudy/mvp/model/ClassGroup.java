package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

//班群
public class ClassGroup implements Serializable {

    public int id;
    public int classId;
    public int classGroupId;
    public String name;
    public String teacher;
    public int state;//1主群2 子群
    public String imageUrl;//课程表
    public boolean isCheck;
    public int teacherId;
    public String subject;
    public int studentCount;

}
