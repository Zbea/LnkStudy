package com.bll.lnkstudy.mvp.model;

import java.util.List;

public class AIScoreItem  {
    public ResultItem result;

    public static class ResultItem {
        public List<MessageItem> choices;

        public static class MessageItem {
            public ContentItem message;

            public static class ContentItem {
                public String content;
            }
        }
    }
}







