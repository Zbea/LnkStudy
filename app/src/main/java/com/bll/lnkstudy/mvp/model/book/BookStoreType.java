package com.bll.lnkstudy.mvp.model.book;

import com.bll.lnkstudy.mvp.model.ItemList;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * 分类
 */
public class BookStoreType {
    public List<ItemList> type;//除开教材分类
    public Map<String,List<ItemList>> subType ;//书籍分类
    public List<ItemList> bookVersion;
}
