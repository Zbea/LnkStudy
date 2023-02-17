package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TeachingVideoType implements Serializable {

    @SerializedName("grade")
    public List<ItemList> grades;
    @SerializedName("semester")
    public List<ItemList> semesters;
    @SerializedName("type")
    public List<ItemList> types;
    public Map<String,List<ItemList>> subType;//子类

}
