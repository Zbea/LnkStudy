package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.painting.PaintingBean;

import java.io.File;
import java.util.List;

public class ItemDetailsBean {

    public String typeStr;
    public int num;
    public List<BookBean> books;
    public List<PaintingBean> paintings;
    public List<File> screens;
}
