package com.bll.lnkstudy.manager;


import android.content.Context;

import com.bll.lnkstudy.R;
import com.bll.lnkstudy.mvp.model.BookStoreType;
import com.bll.lnkstudy.mvp.model.CourseBean;
import com.bll.lnkstudy.mvp.model.DateRemind;
import com.bll.lnkstudy.mvp.model.HomeworkType;
import com.bll.lnkstudy.mvp.model.MainListBean;
import com.bll.lnkstudy.mvp.model.MessageList;
import com.bll.lnkstudy.mvp.model.NoteBook;

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

    public String[] bookType = {
            "诗经楚辞", "唐诗宋词", "经典古文",
            "四大名著","中国科技","小说散文",
            "外国原著","历史地理","政治经济",
            "军事战略","科学技术","艺术才能",
            "运动健康","连环漫画"
    }; //书籍分类

    public String[] ydcy = {
            "运动","健康","棋类",
            "乐器","谱曲","舞蹈",
            "素描","绘画","壁纸",
            "练字","演讲","漫画"
    }; //运动才艺

    public String[] ZRKX={
            "地球天体","物理化学","生命生物"
    };//自然科学

    public String[] SWKX={
           "人工智能","模式识别","心理生理","语言文字","数学"
    };//思维科学

    public Integer[] kmTeachImage={
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

    public Integer[] kmTextbookImage={
            R.mipmap.icon_main_course_yuwen,
            R.mipmap.icon_main_course_shuxue,
            R.mipmap.icon_main_course_yinyu,
            R.mipmap.icon_main_course_wuli,
            R.mipmap.icon_main_course_huaxue,
            R.mipmap.icon_main_course_dili,
            R.mipmap.icon_main_course_sizheng,
            R.mipmap.icon_main_course_lishi,
            R.mipmap.icon_main_course_shengwu,
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
        h1.icon=context.getDrawable(R.mipmap.icon_main_sj1);
        h1.icon_check=context.getDrawable(R.mipmap.icon_main_sj_check1);
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
        h4.icon=context.getDrawable(R.mipmap.icon_main_ks);
        h4.icon_check=context.getDrawable(R.mipmap.icon_main_ks_check);
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

    public List<MessageList> getMessage() {

        List<MessageList> list=new ArrayList<>();
        List<MessageList.MessageBean> listBean=new ArrayList<>();

        MessageList.MessageBean messageBean=new MessageList.MessageBean();
        messageBean.message="数学作业";
        listBean.add(messageBean);
        MessageList.MessageBean messageBean1=new MessageList.MessageBean();
        messageBean1.message="语文作业";
        listBean.add(messageBean1);
        MessageList.MessageBean messageBean2=new MessageList.MessageBean();
        messageBean2.message="英语作业";
        listBean.add(messageBean2);

        MessageList messageList =new MessageList();
        messageList.name="语文周老师";
        messageList.createTime="2020-6-2";
        messageList.content="上交语文作业";
        messageList.messages=listBean;
        list.add(messageList);

        MessageList messageList1 =new MessageList();
        messageList1.name="数学老师";
        messageList1.createTime="2020-6-2";
        messageList1.content="上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业";
        messageList1.messages=listBean;
        list.add(messageList1);

        MessageList messageList2 =new MessageList();
        messageList2.name="妈妈";
        messageList2.createTime="2020-6-2";
        messageList2.content="回家吃饭";
        messageList2.messages=listBean;
        list.add(messageList2);

        return list;
    }

    /**
     * 科目列表
     * @return
     */
    public List<CourseBean> getCourses(){

        List<CourseBean> list=new ArrayList();
        for (int i = 0; i < kmArray.length; i++) {
            CourseBean courseBean =new CourseBean();
            courseBean.name=kmArray[i];
            courseBean.imageId=kmTeachImage[i];
            courseBean.mainCourseId=kmTextbookImage[i];
            courseBean.courseId=i;
            list.add(courseBean);
        }


        return list;

    }


    /**
     * 作业分类列表
     * @return
     */
    public List<HomeworkType> getHomeWorkTypes(boolean isLg, int courseId){

        List<HomeworkType> list=new ArrayList();
        HomeworkType homeWork = new HomeworkType();
        homeWork.name = "随堂作业本";
        homeWork.type = 0;
        homeWork.resId = R.mipmap.icon_homework_zy;
        list.add(homeWork);

        HomeworkType homeWork1 = new HomeworkType();
        homeWork1.name = "家庭作业本";
        homeWork1.type = 1;
        homeWork1.resId = R.mipmap.icon_homework_zy;
        list.add(homeWork1);

        HomeworkType homeWork2 = new HomeworkType();
        homeWork2.name = "课件作业集";
        homeWork2.type = 2;
        homeWork2.resId = R.mipmap.icon_homework_zy;
        list.add(homeWork2);

        if (isLg){
            HomeworkType homeWork3 = new HomeworkType();
            homeWork3.name = "朗读作业本";
            homeWork3.type= 3;
            homeWork3.isListenToRead=true;
            homeWork3.courseId=courseId;
            homeWork3.resId = R.mipmap.icon_homework_ld;
            list.add(homeWork3);
        }

        return list;

    }



    public List<NoteBook> getNoteBook(){
        List<NoteBook> list =new ArrayList<>();

        NoteBook noteBook=new NoteBook();
        noteBook.name="全部笔记";
        noteBook.type=0;
        list.add(noteBook);

        NoteBook noteBook1=new NoteBook();
        noteBook1.name="我的日记";
        noteBook.type=1;
        list.add(noteBook1);

        return list;

    }


    //年级分类
    public List<BookStoreType> getBookTypeGrade(){
        List<BookStoreType> list =new ArrayList<>();

        BookStoreType bookStoreType=new BookStoreType();
        bookStoreType.type=0;
        bookStoreType.title="小学低年级";
        list.add(bookStoreType);

        BookStoreType bookStoreType1=new BookStoreType();
        bookStoreType1.type=1;
        bookStoreType1.title="小学高年级";
        list.add(bookStoreType1);

        BookStoreType bookStoreType2=new BookStoreType();
        bookStoreType2.type=2;
        bookStoreType2.title="初中学生";
        list.add(bookStoreType2);

        BookStoreType bookStoreType3=new BookStoreType();
        bookStoreType3.type=3;
        bookStoreType3.title="高中学生";
        list.add(bookStoreType3);


        return list;
    }

    //教材分类
    public List<BookStoreType> getBookTypeJc(){
        List<BookStoreType> list =new ArrayList<>();

        BookStoreType bookStoreType=new BookStoreType();
        bookStoreType.type=0;
        bookStoreType.title="我的课本";
        list.add(bookStoreType);

        BookStoreType bookStoreType1=new BookStoreType();
        bookStoreType1.type=1;
        bookStoreType1.title="参考课本";
        list.add(bookStoreType1);

        BookStoreType bookStoreType2=new BookStoreType();
        bookStoreType2.type=2;
        bookStoreType2.title="我的课辅";
        list.add(bookStoreType2);

        BookStoreType bookStoreType5=new BookStoreType();
        bookStoreType5.type=3;
        bookStoreType5.title="参考课辅";
        list.add(bookStoreType5);

        BookStoreType bookStoreType3=new BookStoreType();
        bookStoreType3.type=4;
        bookStoreType3.title="字典词典";
        list.add(bookStoreType3);

        BookStoreType bookStoreType4=new BookStoreType();
        bookStoreType4.type=5;
        bookStoreType4.title="公式定理";
        list.add(bookStoreType4);

        return list;
    }

    //古籍分类
    public List<BookStoreType> getBookTypeGj(){
        List<BookStoreType> list =new ArrayList<>();

        BookStoreType bookStoreType=new BookStoreType();
        bookStoreType.type=0;
        bookStoreType.title="诗经楚辞";
        list.add(bookStoreType);

        BookStoreType bookStoreType1=new BookStoreType();
        bookStoreType1.type=1;
        bookStoreType1.title="唐诗宋词";
        list.add(bookStoreType1);

        BookStoreType bookStoreType2=new BookStoreType();
        bookStoreType2.type=2;
        bookStoreType2.title="古代经典";
        list.add(bookStoreType2);

        BookStoreType bookStoreType3=new BookStoreType();
        bookStoreType3.type=3;
        bookStoreType3.title="四大名著";
        list.add(bookStoreType3);

        BookStoreType bookStoreType4=new BookStoreType();
        bookStoreType4.type=4;
        bookStoreType4.title="中国科技";
        list.add(bookStoreType4);

        return list;
    }

    //社会科学分类
    public List<BookStoreType> getBookTypeSHKX(){
        List<BookStoreType> list =new ArrayList<>();

        BookStoreType bookStoreType=new BookStoreType();
        bookStoreType.type=5;
        bookStoreType.title="小说散文";
        list.add(bookStoreType);

        BookStoreType bookStoreType1=new BookStoreType();
        bookStoreType1.type=6;
        bookStoreType1.title="外国原著";
        list.add(bookStoreType1);

        BookStoreType bookStoreType2=new BookStoreType();
        bookStoreType2.type=7;
        bookStoreType2.title="历史地理";
        list.add(bookStoreType2);

        BookStoreType bookStoreType3=new BookStoreType();
        bookStoreType3.type=8;
        bookStoreType3.title="政治经济";
        list.add(bookStoreType3);

        BookStoreType bookStoreType4=new BookStoreType();
        bookStoreType4.type=9;
        bookStoreType4.title="军事战略";
        list.add(bookStoreType4);

        return list;
    }

    //运动才艺
    public List<BookStoreType> getBookTypeYDCY(){
        List<BookStoreType> list =new ArrayList<>();

        for (int i = 0; i < ydcy.length; i++) {
            BookStoreType bookStoreType=new BookStoreType();
            bookStoreType.type= i==0||i==1? 11 : (i==ydcy.length-1? 13:12);
            bookStoreType.title=ydcy[i];
            list.add(bookStoreType);
        }

        return list;
    }

    //思维科学
    public List<BookStoreType> getBookTypeSWKX(){
        List<BookStoreType> list =new ArrayList<>();

        for (int i = 0; i < SWKX.length; i++) {
            BookStoreType bookStoreType=new BookStoreType();
            bookStoreType.type= 10;
            bookStoreType.title=SWKX[i];
            list.add(bookStoreType);
        }

        return list;
    }

    //自然科学
    public List<BookStoreType> getBookTypeZRKX(){
        List<BookStoreType> list =new ArrayList<>();

        for (int i = 0; i < ZRKX.length; i++) {
            BookStoreType bookStoreType=new BookStoreType();
            bookStoreType.type= 10;
            bookStoreType.title=ZRKX[i];
            list.add(bookStoreType);
        }

        return list;
    }

}
