package com.bll.lnkstudy;


import android.content.Context;

import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.BaseTypeBean;
import com.bll.lnkstudy.mvp.model.CourseBean;
import com.bll.lnkstudy.mvp.model.DateRemind;
import com.bll.lnkstudy.mvp.model.DateWeekBean;
import com.bll.lnkstudy.mvp.model.HomeworkType;
import com.bll.lnkstudy.mvp.model.MainListBean;
import com.bll.lnkstudy.mvp.model.MessageList;
import com.bll.lnkstudy.mvp.model.ModuleBean;
import com.bll.lnkstudy.mvp.model.PopWindowBean;
import com.bll.lnkstudy.utils.ToolUtils;

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

    public String[] bookStoreType = {
            "教材",
            "古籍",
            "自然科学",
            "社会科学",
            "思维科学",
            "运动才艺"};

    public String[] teachList = {
            "课程",
            "体育",
            "美术",
            "舞蹈",
            "演讲",
            "编程"};

    public String[] teachTYList = {
            "球类",
            "棋类",
            "田径",
            "体操",
            "跳水",
            "游泳",
            "皮艇",
            "击剑",
            "举重",
            "摔跤",
            "射击",
            "拳击",
            "跆拳道",
            "自行车",
            "滑雪",
            "滑冰",
            "冰球",
            "冰壶"};

    public String[] teachMSList = {
            "绘画",
            "雕塑",
            "书法",
            "篆刻",
            "建筑",
            "摄影",
            "工艺",
            "设计"};

    public String[] teachWDList = {
            "芭蕾",
            "名族",
            "现代",
            "古典",
            "拉丁",
            "交际",
            "爵士",
            "街舞"};

    public String[] teachYJList = {
            "命题",
            "即兴",
            "辩论",
            "背诵"};

    public String[] teachBCList = {
            "go",
            "Android",
            "python",
            "swift"};


    public Integer[] dateRemind={1,3,5,7,10,15};

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
            "四大名著", "中国科技", "小说散文",
            "外国原著", "历史地理", "政治经济",
            "军事战略", "科学技术", "艺术才能",
            "运动健康", "连环漫画"
    }; //书籍分类

    public String[] ydcy = {
            "运动", "健康", "棋类",
            "乐器", "谱曲", "舞蹈",
            "素描", "绘画", "壁纸",
            "练字", "演讲", "漫画"
    }; //运动才艺

    public String[] ZRKX = {
            "地球天体", "物理化学", "生命生物"
    };//自然科学

    public String[] SWKX = {
            "人工智能", "模式识别", "心理生理", "语言文字", "数学"
    };//思维科学


    public String[] YEARS = {
            "汉朝", "唐朝", "宋朝", "元朝", "明朝","清朝","近代","当代"
    };

    public String[] PAINTING = {
            "毛笔书法", "山水画", "花鸟画", "人物画"
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
        h0.icon = context.getDrawable(R.mipmap.icon_main_sy);
        h0.icon_check = context.getDrawable(R.mipmap.icon_main_sy_check);
        h0.checked = true;
        h0.name = listTitle[0];

        MainListBean h1 = new MainListBean();
        h1.icon = context.getDrawable(R.mipmap.icon_main_sj);
        h1.icon_check = context.getDrawable(R.mipmap.icon_main_sj_check);
        h1.checked = false;
        h1.name = listTitle[1];

        MainListBean h2 = new MainListBean();
        h2.icon = context.getDrawable(R.mipmap.icon_main_kb);
        h2.icon_check = context.getDrawable(R.mipmap.icon_main_kb_check);
        h2.checked = false;
        h2.name = listTitle[2];

        MainListBean h3 = new MainListBean();
        h3.icon = context.getDrawable(R.mipmap.icon_main_zy);
        h3.icon_check = context.getDrawable(R.mipmap.icon_main_zy_check);
        h3.checked = false;
        h3.name = listTitle[3];

        MainListBean h4 = new MainListBean();
        h4.icon = context.getDrawable(R.mipmap.icon_main_ks);
        h4.icon_check = context.getDrawable(R.mipmap.icon_main_ks_check);
        h4.checked = false;
        h4.name = listTitle[4];

        MainListBean h5 = new MainListBean();
        h5.icon = context.getDrawable(R.mipmap.icon_main_bj);
        h5.icon_check = context.getDrawable(R.mipmap.icon_main_bj_check);
        h5.checked = false;
        h5.name = listTitle[5];


        MainListBean h6 = new MainListBean();
        h6.icon = context.getDrawable(R.mipmap.icon_main_sh);
        h6.icon_check = context.getDrawable(R.mipmap.icon_main_sh_check);
        h6.checked = false;
        h6.name = listTitle[6];

        MainListBean h7 = new MainListBean();
        h7.icon = context.getDrawable(R.mipmap.icon_main_yj);
        h7.icon_check = context.getDrawable(R.mipmap.icon_main_yj_check);
        h7.checked = false;
        h7.name = listTitle[7];

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

    public List<DateRemind> getRemind() {
        List<DateRemind> list = new ArrayList<DateRemind>();
        for (int i:dateRemind) {
            DateRemind dateRemind=new DateRemind();
            dateRemind.remind=i+"天";
            dateRemind.remindIn=i;
            dateRemind.isCheck=i==1;
            list.add(dateRemind);
        }
        return list;
    }

    public List<DateWeekBean> getWeeks(){
        List<DateWeekBean> list = new ArrayList<>();

        list.add(new DateWeekBean("周一","MO",2,false));
        list.add(new DateWeekBean("周二","TU",3,false));
        list.add(new DateWeekBean("周三","WE",4,false));
        list.add(new DateWeekBean("周四","TH",5,false));
        list.add(new DateWeekBean("周五","FR",6,false));
        list.add(new DateWeekBean("周六","SA",7,false));
        list.add(new DateWeekBean("周日","SU",8,false));

        return list;
    }



    public List<MessageList> getMessage() {

        List<MessageList> list = new ArrayList<>();

        MessageList messageList = new MessageList();
        messageList.name = "语文周老师";
        messageList.createTime = "2020-6-2";
        messageList.content = "上交语文作业";
        list.add(messageList);

        MessageList messageList1 = new MessageList();
        messageList1.name = "数学老师";
        messageList1.createTime = "2020-6-2";
        messageList1.content = "上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业";
        list.add(messageList1);

        MessageList messageList2 = new MessageList();
        messageList2.name = "妈妈";
        messageList2.createTime = "2020-6-2";
        messageList2.content = "回家吃饭";
        list.add(messageList2);

        return list;
    }

    /**
     * 科目列表
     *
     * @return
     */
    public List<CourseBean> getCourses() {

        List<CourseBean> list = new ArrayList();
        for (int i = 0; i < kmArray.length; i++) {
            CourseBean courseBean = new CourseBean();
            courseBean.name = kmArray[i];
            courseBean.courseId = i;
            list.add(courseBean);
        }
        return list;

    }


    /**
     * 作业分类列表
     *
     * @return
     */
    public List<HomeworkType> getHomeWorkTypes( int courseId, int grade) {
        int resId = 0;
        if (courseId == 0) {
            if (grade < 4) {
                resId = R.mipmap.icon_homework_yw_tzb;
            } else {
                resId = R.mipmap.icon_homework_yw_zxzyb;
            }
        } else if (courseId == 1) {
            if (grade < 4) {
                resId = R.mipmap.icon_homework_sx_sxb;
            } else {
                resId = R.mipmap.icon_homework_other_lxb;
            }
        } else if (courseId == 2) {
            if (grade < 4) {
                resId = R.mipmap.icon_homework_yy_xxyyb;
            } else {
                resId = R.mipmap.icon_homework_yy_zxyyb;
            }
        } else {
            resId = R.mipmap.icon_homework_other_lxb;
        }

        List<HomeworkType> list = new ArrayList();
        HomeworkType homeWork = new HomeworkType();
        homeWork.name = "课堂作业本";
        homeWork.typeId = 0;
        homeWork.courseId = courseId;
        homeWork.bgResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_1) ;
        homeWork.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId) ;
        list.add(homeWork);

        HomeworkType homeWork1 = new HomeworkType();
        homeWork1.name = "课外作业本";
        homeWork1.typeId = 1;
        homeWork1.courseId = courseId;
        homeWork1.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_2);
        homeWork1.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId);
        list.add(homeWork1);

        HomeworkType homeWork2 = new HomeworkType();
        homeWork2.name = "课堂题卷本";
        homeWork2.typeId = 2;
        homeWork2.state=2;
        homeWork2.courseId = courseId;
        homeWork2.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_3);
        homeWork2.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId);
        list.add(homeWork2);

        if (courseId==0||courseId==2) {
            HomeworkType homeWork4 = new HomeworkType();
            homeWork4.name = "课文朗读册";
            homeWork4.typeId = 4;
            homeWork4.state=1;
            homeWork4.courseId = courseId;
            homeWork4.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_4);
            list.add(homeWork4);
        }

        return list;

    }



    //年级分类
    public List<PopWindowBean> getBookTypeGrade() {
        List<PopWindowBean> list = new ArrayList<>();

        PopWindowBean baseTypeBean = new PopWindowBean();
        baseTypeBean.id = 0;
        baseTypeBean.name = "小学低年级";
        baseTypeBean.isCheck=true;
        list.add(baseTypeBean);

        PopWindowBean baseTypeBean1 = new PopWindowBean();
        baseTypeBean1.id = 1;
        baseTypeBean1.name = "小学高年级";
        list.add(baseTypeBean1);

        PopWindowBean baseTypeBean2 = new PopWindowBean();
        baseTypeBean2.id = 2;
        baseTypeBean2.name = "初中学生";
        list.add(baseTypeBean2);

        PopWindowBean baseTypeBean3 = new PopWindowBean();
        baseTypeBean3.id = 3;
        baseTypeBean3.name = "高中学生";
        list.add(baseTypeBean3);


        return list;
    }

    //教材分类
    public List<BaseTypeBean> getBookTypeJc() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 0;
        baseTypeBean.name = "我的课本";
        list.add(baseTypeBean);
        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 1;
        baseTypeBean1.name = "我的课辅";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 2;
        baseTypeBean2.name = "参考课本";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 3;
        baseTypeBean3.name = "参考课辅";
        list.add(baseTypeBean3);

        return list;
    }

    //古籍分类
    public List<BaseTypeBean> getBookTypeGj() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 0;
        baseTypeBean.name = "诗经楚辞";
        list.add(baseTypeBean);

        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 1;
        baseTypeBean1.name = "唐诗宋词";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 2;
        baseTypeBean2.name = "古代经典";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 3;
        baseTypeBean3.name = "四大名著";
        list.add(baseTypeBean3);

        BaseTypeBean baseTypeBean4 = new BaseTypeBean();
        baseTypeBean4.typeId = 4;
        baseTypeBean4.name = "中国科技";
        list.add(baseTypeBean4);

        return list;
    }

    //社会科学分类
    public List<BaseTypeBean> getBookTypeSHKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 5;
        baseTypeBean.name = "小说散文";
        list.add(baseTypeBean);

        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 6;
        baseTypeBean1.name = "外国原著";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 7;
        baseTypeBean2.name = "历史地理";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 8;
        baseTypeBean3.name = "政治经济";
        list.add(baseTypeBean3);

        BaseTypeBean baseTypeBean4 = new BaseTypeBean();
        baseTypeBean4.typeId = 9;
        baseTypeBean4.name = "军事战略";
        list.add(baseTypeBean4);

        return list;
    }

    //运动才艺
    public List<BaseTypeBean> getBookTypeYDCY() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < ydcy.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = i == 0 || i == 1 ? 11 : (i == ydcy.length - 1 ? 13 : 12);
            baseTypeBean.name = ydcy[i];
            list.add(baseTypeBean);
        }

        return list;
    }

    //思维科学
    public List<BaseTypeBean> getBookTypeSWKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < SWKX.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = 10;
            baseTypeBean.name = SWKX[i];
            list.add(baseTypeBean);
        }

        return list;
    }

    //自然科学
    public List<BaseTypeBean> getBookTypeZRKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < ZRKX.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = 10;
            baseTypeBean.name = ZRKX[i];
            list.add(baseTypeBean);
        }

        return list;
    }


    //语文作业本
    public List<ModuleBean> getYw(int grade) {
        List<ModuleBean> list = new ArrayList<>();
        if (grade <= 3) {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "拼音田字本";
            moduleBean.resId = R.mipmap.icon_homework_yw_pytzb_1;
            moduleBean.resContentId = R.mipmap.icon_homework_yw_pytzb;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "田字本";
            moduleBean1.resId = R.mipmap.icon_homework_yw_tzb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_yw_tzb;

            ModuleBean moduleBean2 = new ModuleBean();
            moduleBean2.name = "拼音本";
            moduleBean2.resId = R.mipmap.icon_homework_yw_pyb_1;
            moduleBean2.resContentId = R.mipmap.icon_homework_yw_pyb;

            ModuleBean moduleBean3 = new ModuleBean();
            moduleBean3.name = "写字本";
            moduleBean3.resId = R.mipmap.icon_homework_yw_xzb_1;
            moduleBean3.resContentId = R.mipmap.icon_homework_yw_xzb;

            list.add(moduleBean);
            list.add(moduleBean1);
            list.add(moduleBean2);
            list.add(moduleBean3);
        } else {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "中学练习本";
            moduleBean.resId = R.mipmap.icon_homework_other_lxb_1;
            moduleBean.resContentId = R.mipmap.icon_homework_other_lxb;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "中学作文本";
            moduleBean1.resId = R.mipmap.icon_homework_yw_zxzyb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_yw_zxzyb;

            list.add(moduleBean);
            list.add(moduleBean1);
        }
        return list;
    }

    //数学作业本
    public List<ModuleBean> getSx(int grade) {
        List<ModuleBean> list = new ArrayList<>();
        if (grade <= 3) {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "空白";
            moduleBean.resId = R.drawable.bg_black_stroke_5dp_corner;
            moduleBean.resContentId = 0;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "小学数学本";
            moduleBean1.resId = R.mipmap.icon_homework_sx_sxb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_sx_sxb;

            list.add(moduleBean);
            list.add(moduleBean1);

        } else {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "空白";
            moduleBean.resId = R.drawable.bg_black_stroke_5dp_corner;
            moduleBean.resContentId = 0;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "中学练习本";
            moduleBean1.resId = R.mipmap.icon_homework_other_lxb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_other_lxb;

            list.add(moduleBean);
            list.add(moduleBean1);
        }
        return list;
    }

    //英语作业本
    public List<ModuleBean> getYy(int grade) {
        List<ModuleBean> list = new ArrayList<>();
        if (grade <= 3) {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "空白";
            moduleBean.resId = R.drawable.bg_black_stroke_5dp_corner;
            moduleBean.resContentId = 0;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "小学英语本";
            moduleBean1.resId = R.mipmap.icon_homework_yy_xxyyb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_yy_xxyyb;

            list.add(moduleBean);
            list.add(moduleBean1);

        } else {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.name = "中学英语本";
            moduleBean.resId = R.mipmap.icon_homework_yy_zxyyb_1;
            moduleBean.resContentId = R.mipmap.icon_homework_yy_zxyyb;

            ModuleBean moduleBean1 = new ModuleBean();
            moduleBean1.name = "中学练习本";
            moduleBean1.resId = R.mipmap.icon_homework_other_lxb_1;
            moduleBean1.resContentId = R.mipmap.icon_homework_other_lxb;

            list.add(moduleBean);
            list.add(moduleBean1);
        }
        return list;
    }

    //其他本子
    public List<ModuleBean> getOther() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.name = "空白";
        moduleBean.resId = R.drawable.bg_black_stroke_5dp_corner;
        moduleBean.resContentId = 0;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.name = "中学练习本";
        moduleBean1.resId = R.mipmap.icon_homework_other_lxb_1;
        moduleBean1.resContentId = R.mipmap.icon_homework_other_lxb;

        list.add(moduleBean);
        list.add(moduleBean1);
        return list;
    }

    //封面
    public List<ModuleBean> getHomeworkCover() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.resId = R.mipmap.icon_homework_cover_1;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.resId = R.mipmap.icon_homework_cover_2;

        ModuleBean moduleBean2 = new ModuleBean();
        moduleBean2.resId = R.mipmap.icon_homework_cover_3;

        ModuleBean moduleBean3 = new ModuleBean();
        moduleBean3.resId = R.mipmap.icon_homework_cover_4;

        list.add(moduleBean);
        list.add(moduleBean1);
        list.add(moduleBean2);
        list.add(moduleBean3);
        return list;
    }

    public List<AppBean> getAppBaseList(){
        List<AppBean> apps=new ArrayList<>();
        AppBean appBean=new AppBean();
        appBean.appId=0;
        appBean.appName="应用市场";
        appBean.image= MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_center);
        appBean.isBase=true;
        apps.add(appBean);

        AppBean appBean1=new AppBean();
        appBean1.appId=1;
        appBean1.appName="官方工具";
        appBean1.image= MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_tool);
        appBean1.isBase=true;
        apps.add(appBean1);

        AppBean appBean2=new AppBean();
        appBean2.appId=2;
        appBean2.appName="官方壁纸";
        appBean2.image= MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_wallpaper);
        appBean2.isBase=true;
        apps.add(appBean2);

        AppBean appBean3=new AppBean();
        appBean3.appId=3;
        appBean3.appName="官方书画";
        appBean3.image= MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_painting);
        appBean3.isBase=true;
        apps.add(appBean3);

        AppBean appBean4=new AppBean();
        appBean4.appId=4;
        appBean4.appName="官方书籍";
        appBean4.image= MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_cz);
        appBean4.isBase=true;
        apps.add(appBean4);

        return apps;
    }


    //基础笔记分类
    public List<BaseTypeBean> getNoteBook() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean noteBook = new BaseTypeBean();
        noteBook.name = "我的日记";
        noteBook.typeId = 0;
        list.add(noteBook);

        BaseTypeBean noteBook1 = new BaseTypeBean();
        noteBook1.name = "金句彩段";
        noteBook1.typeId = 1;
        list.add(noteBook1);

        BaseTypeBean noteBook2 = new BaseTypeBean();
        noteBook2.name = "典型题型";
        noteBook2.typeId = 2;
        list.add(noteBook2);

        return list;

    }

    //日记内容选择
    public List<ModuleBean> getNoteModuleDiary() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.name = "横格本";
        moduleBean.resId = R.mipmap.icon_note_module_bg_1;
        moduleBean.resContentId =  R.mipmap.icon_note_details_bg_6;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.name = "方格本";
        moduleBean1.resId = R.mipmap.icon_note_module_bg_2;
        moduleBean1.resContentId = R.mipmap.icon_note_details_bg_7;

        list.add(moduleBean);
        list.add(moduleBean1);
        return list;
    }

    //笔记本内容选择
    public List<ModuleBean> getNoteModuleBook() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.name = "空白本";
        moduleBean.resId = R.drawable.bg_gray_stroke_10dp_corner;
        moduleBean.resContentId =  0;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.name = "横格本";
        moduleBean1.resId = R.mipmap.icon_note_module_bg_1;
        moduleBean1.resContentId = R.mipmap.icon_note_details_bg_1;

        ModuleBean moduleBean2 = new ModuleBean();
        moduleBean2.name = "方格本";
        moduleBean2.resId = R.mipmap.icon_note_module_bg_2;
        moduleBean2.resContentId = R.mipmap.icon_note_details_bg_2;

        ModuleBean moduleBean3 = new ModuleBean();
        moduleBean3.name = "英语本";
        moduleBean3.resId = R.mipmap.icon_note_module_bg_3;
        moduleBean3.resContentId = R.mipmap.icon_note_details_bg_3;

        ModuleBean moduleBean4 = new ModuleBean();
        moduleBean4.name = "田字本";
        moduleBean4.resId = R.mipmap.icon_note_module_bg_4;
        moduleBean4.resContentId = R.mipmap.icon_note_details_bg_4;

        ModuleBean moduleBean5 = new ModuleBean();
        moduleBean5.name = "五线谱";
        moduleBean5.resId = R.mipmap.icon_note_module_bg_5;
        moduleBean5.resContentId = R.mipmap.icon_note_details_bg_5;

        list.add(moduleBean);
        list.add(moduleBean1);
        list.add(moduleBean2);
        list.add(moduleBean3);
        list.add(moduleBean4);
        list.add(moduleBean5);
        return list;
    }

}
