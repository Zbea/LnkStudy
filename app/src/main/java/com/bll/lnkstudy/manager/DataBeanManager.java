package com.bll.lnkstudy.manager;


import android.content.Context;

import com.bll.lnkstudy.R;
import com.bll.lnkstudy.mvp.model.CourseList;
import com.bll.lnkstudy.mvp.model.DateRemind;
import com.bll.lnkstudy.mvp.model.HomeWork;
import com.bll.lnkstudy.mvp.model.MainListBean;
import com.bll.lnkstudy.mvp.model.MessageBean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DataBeanManager {

    private static DataBeanManager incetance = null;

    public static DataBeanManager getIncetance() {

        if (incetance == null) {
            synchronized (DataBeanManager.class) {
                if (incetance == null) {
                    incetance = new DataBeanManager();
                }
            }
        }
        return incetance;
    }

    private String[] listTitle = {
            "首页",
            "书架",
            "课本",
            "作业",
            "考卷",
            "笔记",
            "书画",
            "义教"};

    public String[] dateDayTitle = {
            "不重复",
            "每月",
            "每年"};
    public String[] dateScheduleTitle = {
            "不重复",
            "每天",
            "每周",
            "每月",
            "每年"};

    public String[] kmArray = {
            "语文",
            "数学",
            "英语",
            "物理",
            "化学",
            "地理",
            "政治",
            "历史",
            "生物",
    }; //科目的数据

    public Integer[] kmImage={
            R.mipmap.icon_teach_yuwen,
            R.mipmap.icon_teach_shuxue,
            R.mipmap.icon_teach_yinwen,
            R.mipmap.icon_teach_wuli,
            R.mipmap.icon_teach_huaxue,
            R.mipmap.icon_teach_dili,
            R.mipmap.icon_teach_sizhen,
            R.mipmap.icon_teach_lishi,
            R.mipmap.icon_teach_shengwu,
    };

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    public ArrayList<MainListBean> getIndexData(Context context) {

        ArrayList<MainListBean> list = new ArrayList<>();

        MainListBean h0 = new MainListBean();
        h0.icon=context.getDrawable(R.mipmap.icon_main_sy);
        h0.icon_check=context.getDrawable(R.mipmap.icon_main_sy_check);
        h0.checked=true;
        h0.name=listTitle[0];

        MainListBean h1 = new MainListBean();
        h1.icon=context.getDrawable(R.mipmap.icon_main_sj);
        h1.icon_check=context.getDrawable(R.mipmap.icon_main_sj_check);
        h1.checked=false;
        h1.name=listTitle[1];

        MainListBean h2 = new MainListBean();
        h2.icon=context.getDrawable(R.mipmap.icon_main_kb);
        h2.icon_check=context.getDrawable(R.mipmap.icon_main_kb_check);
        h2.checked=false;
        h2.name=listTitle[2];

        MainListBean h3= new MainListBean();
        h3.icon=context.getDrawable(R.mipmap.icon_main_zy);
        h3.icon_check=context.getDrawable(R.mipmap.icon_main_zy_check);
        h3.checked=false;
        h3.name=listTitle[3];

        MainListBean h4 = new MainListBean();
        h4.icon=context.getDrawable(R.mipmap.icon_main_kf);
        h4.icon_check=context.getDrawable(R.mipmap.icon_main_kf_check);
        h4.checked=false;
        h4.name=listTitle[4];

        MainListBean h5 = new MainListBean();
        h5.icon=context.getDrawable(R.mipmap.icon_main_bj);
        h5.icon_check=context.getDrawable(R.mipmap.icon_main_bj_check);
        h5.checked=false;
        h5.name=listTitle[5];


        MainListBean h6 = new MainListBean();
        h6.icon=context.getDrawable(R.mipmap.icon_main_sh);
        h6.icon_check=context.getDrawable(R.mipmap.icon_main_sh_check);
        h6.checked=false;
        h6.name=listTitle[6];

        MainListBean h7= new MainListBean();
        h7.icon=context.getDrawable(R.mipmap.icon_main_yj);
        h7.icon_check=context.getDrawable(R.mipmap.icon_main_yj_check);
        h7.checked=false;
        h7.name=listTitle[7];

        list.add(h0);
        list.add(h1);
        list.add(h2);
        list.add(h3);
        list.add(h4);
        list.add(h5);
        list.add(h6);
        list.add(h7);

        return list;
    }

    /**
     * 重要日子提醒事件
     * @return
     */
    public List<DateRemind> getRemindDay() {
        List<DateRemind> list=new ArrayList<DateRemind>();
        DateRemind remindBean=new DateRemind();
        remindBean.remind="当天（上午9点）";
        remindBean.remindIn=0;
        list.add(remindBean);

        DateRemind remindBean1=new DateRemind();
        remindBean1.remind="1天前（上午9点）";
        remindBean1.remindIn=1;
        list.add(remindBean1);

        DateRemind remindBean2=new DateRemind();
        remindBean2.remind="2天前（上午9点）";
        remindBean2.remindIn=2;
        list.add(remindBean2);

        DateRemind remindBean3=new DateRemind();
        remindBean3.remind="3天前（上午9点）";
        remindBean3.remindIn=3;
        list.add(remindBean3);

        return list;
    }
    /**
     * 日程提醒事件
     * @return
     */
    public List<DateRemind> getRemindSchedule() {
        List<DateRemind> list=new ArrayList<DateRemind>();
        DateRemind remindBean=new DateRemind();
        remindBean.remind="提前5分钟";
        remindBean.remindIn=1;
        list.add(remindBean);

        DateRemind remindBean1=new DateRemind();
        remindBean1.remind="提前10分钟";
        remindBean1.remindIn=2;
        list.add(remindBean1);

        DateRemind remindBean2=new DateRemind();
        remindBean2.remind="提前15分钟";
        remindBean2.remindIn=3;
        list.add(remindBean2);

        DateRemind remindBean3=new DateRemind();
        remindBean3.remind="提前20分钟";
        remindBean3.remindIn=4;
        list.add(remindBean3);

        DateRemind remindBean4=new DateRemind();
        remindBean4.remind="提前25分钟";
        remindBean4.remindIn=5;
        list.add(remindBean4);

        DateRemind remindBean5=new DateRemind();
        remindBean5.remind="提前30分钟";
        remindBean5.remindIn=6;
        list.add(remindBean5);

        return list;
    }

    public List<MessageBean> getMessage() {

        List<MessageBean> list=new ArrayList<>();

        MessageBean messageBean=new MessageBean();
        messageBean.name="语文周老师";
        messageBean.createTime="2020-6-2";
        messageBean.content="上交语文作业";
        list.add(messageBean);

        MessageBean messageBean1=new MessageBean();
        messageBean1.name="数学老师";
        messageBean1.createTime="2020-6-2";
        messageBean1.content="上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业";
        list.add(messageBean1);

        MessageBean messageBean2=new MessageBean();
        messageBean2.name="妈妈";
        messageBean2.createTime="2020-6-2";
        messageBean2.content="回家吃饭";
        list.add(messageBean2);

        return list;
    }

    /**
     * 科目列表
     * @return
     */
    public List<CourseList> getCourses(){

        List<CourseList> list=new ArrayList();
        for (int i = 0; i < kmArray.length; i++) {
            CourseList courseList=new CourseList();
            courseList.name=kmArray[i];
            courseList.imageId=kmImage[i];
            list.add(courseList);
        }


        return list;

    }

    /**
     * 获取作业内容分类
     * @return
     */
    public List<HomeWork> getHomeworkType(){
        List<HomeWork> list=new ArrayList();

        HomeWork homeWork=new HomeWork();
        homeWork.title="随堂作业本";
        homeWork.isPg=true;
        homeWork.type=0;
        homeWork.resId=R.mipmap.icon_homework_st;
        list.add(homeWork);

        HomeWork homeWork1=new HomeWork();
        homeWork1.title="课件作业集";
        homeWork1.type=1;
        homeWork1.resId=R.mipmap.icon_homework_kj;
        list.add(homeWork1);

        HomeWork homeWork2=new HomeWork();
        homeWork2.title="家庭作业本";
        homeWork2.type=2;
        homeWork2.resId=R.mipmap.icon_homework_jt;
        list.add(homeWork2);

        HomeWork homeWork4=new HomeWork();
        homeWork4.title="我的课辅本";
        homeWork4.type=3;
        homeWork4.resId=R.mipmap.icon_homework_kf;
        list.add(homeWork4);

        HomeWork homeWork5=new HomeWork();
        homeWork5.title="朗读作业本";
        homeWork5.type=4;
        homeWork5.resId=R.mipmap.icon_homework_kf;
        list.add(homeWork5);

        HomeWork homeWork6=new HomeWork();
        homeWork6.title="实验报告";
        homeWork6.type=5;
        homeWork6.resId=R.mipmap.icon_homework_pg;
        list.add(homeWork6);

        HomeWork homeWork7=new HomeWork();
        homeWork7.title="社会实践";
        homeWork7.type=6;
        homeWork7.resId=R.mipmap.icon_homework_sj;
        list.add(homeWork7);

        return list;
    }

}
