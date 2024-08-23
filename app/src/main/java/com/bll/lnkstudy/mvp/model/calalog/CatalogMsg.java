package com.bll.lnkstudy.mvp.model.calalog;

import java.util.List;

public class CatalogMsg {

    /**
     * title : 三年级上学期-数学
     * totalCount : 15
     * contents : [{"title":"第一章","pageNumber":1,"picName":"1.jpg","subItmes":[{"title":"第一节","pageNumber":1,"picName":"1.jpg","subItmes":[]}]},{"title":"第二章","pageNumber":4,"picName":"4.jpg","subItems":[{"title":"第一节","pageNumber":4,"picName":"4.jpg","subItems":[]},{"title":"第二节","pageNumber":7,"picName":"7.jpg","subItems":[]},{"title":"第三节","pageNumber":11,"picName":"11.jpg","subItems":[]}]},{"title":"第三章","pageNumber":15,"picName":"15.jpg","subItems":[]}]
     */

    public String title;
    public int totalCount;
    public int startCount=1;//目录正式开始的页面（扣除前序）
    public List<ContentsBean> contents;

    /**
     * title : 第一章
     * pageNumber : 1
     * picName : 1.jpg
     * subItmes : [{"title":"第一节","pageNumber":1,"picName":"1.jpg","subItmes":[]}]
     */
    public static class ContentsBean {
        public String title;
        public int pageNumber;
        public String picName;
        public List<SubItemsBean> subItems;

        /**
         * title : 第一节
         * pageNumber : 1
         * picName : 1.jpg
         * subItmes : []
         */
        public static class SubItemsBean {
            public String title;
            public int pageNumber;
            public String picName;
        }
    }
}
