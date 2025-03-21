package com.bll.lnkstudy.mvp.model.calalog;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogParentBean extends AbstractExpandableItem<CatalogChildBean> implements Serializable, MultiItemEntity {

    public String title;
    public int pageNumber;
    public String picName;


    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
