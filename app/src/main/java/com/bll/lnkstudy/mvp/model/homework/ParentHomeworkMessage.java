package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ParentHomeworkMessage implements Serializable {

    public int total;
    public List<ParentHomeworkBean> list;
}
