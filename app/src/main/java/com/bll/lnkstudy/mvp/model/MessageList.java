package com.bll.lnkstudy.mvp.model;

import java.util.List;

public class MessageList {

    public int id;
    public String name;
    public String content;
    public String createTime;
    public boolean isLook;
    public boolean isCheck;
    public List<MessageBean> messages;

    public static class MessageBean{

        public int id;
        public String message;

    }

}
