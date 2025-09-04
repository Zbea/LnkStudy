package com.bll.lnkstudy.mvp.model;

import java.util.ArrayList;
import java.util.List;

public class AiRequestItem {
    public String type="vision";
    public boolean thinking=true;
    public String subject;
    public String prompt;
    public List<ImageItem> question=new ArrayList<>();
    public List<ImageItem> answer=new ArrayList<>();

    public static class ImageItem  {
        public String type="image_url";
        public ImageUrlItem image_url;

        public static class ImageUrlItem {
            public String url;
        }
    }

}

