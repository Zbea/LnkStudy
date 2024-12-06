package com.bll.lnkstudy.mvp.model.homework;

import java.util.List;

/**
 * 首页作业通知
 */
public class HomeworkNoticeList {

    public int total;
    public List<HomeworkNoticeBean> list;

    public static class HomeworkNoticeBean  {
        public int id;
        public String title;//作业标题
        public String typeName;//作业分类
        public long endTime;//是否需要提交的时间
        public long time;//布置时间
        public int subject;
        public String name;
        public int type;//1家长2老师3学校
        public int correctMode;//批改模式
        public int scoreMode;//打分模式1打分
        public double score;//成绩
        public String correctJson;//批改详情
        public String answerUrl;
        public String correctUrl;
    }

}
