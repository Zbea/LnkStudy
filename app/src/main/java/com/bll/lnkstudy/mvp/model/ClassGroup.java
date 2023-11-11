package com.bll.lnkstudy.mvp.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

//班群
public class ClassGroup implements Serializable {

    public int id;
    public int classNum;//群号
    public String name;
    public String teacher;
    public int teacherId;
    public String subject;
    public int classId;
    public long date;
    public int status;//1可以双通 0不可以

    public boolean isCheck;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof ClassGroup))
            return false;
        if (this==obj)
            return true;
        ClassGroup classGroup=(ClassGroup) obj;
        return this.id==classGroup.id&& Objects.equals(this.name, classGroup.name) && Objects.equals(this.classNum, classGroup.classNum)
                &&Objects.equals(this.teacher, classGroup.teacher)&&this.teacherId==classGroup.teacherId&&Objects.equals(this.subject, classGroup.subject)
                &&this.classId==classGroup.classId&&this.date==classGroup.date&&this.status==classGroup.status;
    }
}
