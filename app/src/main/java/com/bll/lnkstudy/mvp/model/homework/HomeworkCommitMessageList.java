package com.bll.lnkstudy.mvp.model.homework;

import java.util.List;

public class HomeworkCommitMessageList {
    public int total;
    public List<CommitMessageBean> list;

    public static class CommitMessageBean{
        public int id;
        public String title;
        public String typeName;
        public long time;
        public int subject;
    }
}
