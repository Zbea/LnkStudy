package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * 分类
 */
public class BookStoreType {
    public List<String> grade;//全部年级
    public List<String> typeGrade;//书籍部分年级
    public List<String> type;//除开教材分类
    public Map<String,List<String>> subType ;//书籍分类
    public FontDrawTypeBean fontDrawType;//书画分类
    public List<OfficialBean> appType;//apk官方、第三方

    private class FontDrawTypeBean {

        public TypeBean types;

        private class TypeBean{
            public List<CategoryBean> category;//1壁纸2书画
            public List<ClassifyBean> classify;//书画内容分类
            public List<DynastyBean> dynasty;
            public List<SupplyBean> supply;//官方

            private class CategoryBean {
                public int type;
                public String desc;
            }
            private class ClassifyBean {
                public int type;
                public String desc;
            }
            private class DynastyBean {
                public int type;
                public String desc;
            }
            private class SupplyBean {
                public int type;
                public String desc;
            }
        }

    }

    private class OfficialBean {
        public int type;
        public String desc;
    }
}
