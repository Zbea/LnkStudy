package com.bll.lnkstudy;


import android.content.Context;

import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.BaseTypeBean;
import com.bll.lnkstudy.mvp.model.CourseBean;
import com.bll.lnkstudy.mvp.model.DateRemind;
import com.bll.lnkstudy.mvp.model.DateWeek;
import com.bll.lnkstudy.mvp.model.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.MainList;
import com.bll.lnkstudy.mvp.model.MessageList;
import com.bll.lnkstudy.mvp.model.Module;
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

    public String[] textbookType={
            "我的课本",
            "我的课辅",
            "参考教材",
            "往期教材"
    };

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
            "诗经楚辞", "唐诗宋词", "古代经典",
            "四大名著", "中国科技", "小说散文",
            "外国原著", "历史地理", "政治经济",
            "军事战略", "科学技术", "运动才艺"
    }; //书籍分类

    public String[] YEARS = {
            "汉朝", "唐朝", "宋朝", "元朝", "明朝","清朝","近代","当代"
    };

    public String[] PAINTING = {
            "毛笔书法", "山水画", "花鸟画", "人物画","素描画","硬笔书法"
    };

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    public ArrayList<MainList> getIndexData(Context context) {

        ArrayList<MainList> list = new ArrayList<>();

        MainList h0 = new MainList();
        h0.icon = context.getDrawable(R.mipmap.icon_main_sy);
        h0.icon_check = context.getDrawable(R.mipmap.icon_main_sy_check);
        h0.checked = true;
        h0.name = listTitle[0];

        MainList h1 = new MainList();
        h1.icon = context.getDrawable(R.mipmap.icon_main_sj);
        h1.icon_check = context.getDrawable(R.mipmap.icon_main_sj_check);
        h1.checked = false;
        h1.name = listTitle[1];

        MainList h2 = new MainList();
        h2.icon = context.getDrawable(R.mipmap.icon_main_kb);
        h2.icon_check = context.getDrawable(R.mipmap.icon_main_kb_check);
        h2.checked = false;
        h2.name = listTitle[2];

        MainList h3 = new MainList();
        h3.icon = context.getDrawable(R.mipmap.icon_main_zy);
        h3.icon_check = context.getDrawable(R.mipmap.icon_main_zy_check);
        h3.checked = false;
        h3.name = listTitle[3];

        MainList h4 = new MainList();
        h4.icon = context.getDrawable(R.mipmap.icon_main_ks);
        h4.icon_check = context.getDrawable(R.mipmap.icon_main_ks_check);
        h4.checked = false;
        h4.name = listTitle[4];

        MainList h5 = new MainList();
        h5.icon = context.getDrawable(R.mipmap.icon_main_bj);
        h5.icon_check = context.getDrawable(R.mipmap.icon_main_bj_check);
        h5.checked = false;
        h5.name = listTitle[5];


        MainList h6 = new MainList();
        h6.icon = context.getDrawable(R.mipmap.icon_main_sh);
        h6.icon_check = context.getDrawable(R.mipmap.icon_main_sh_check);
        h6.checked = false;
        h6.name = listTitle[6];

        MainList h7 = new MainList();
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

    public List<DateWeek> getWeeks(){
        List<DateWeek> list = new ArrayList<>();

        list.add(new DateWeek("周一","MO",2,false));
        list.add(new DateWeek("周二","TU",3,false));
        list.add(new DateWeek("周三","WE",4,false));
        list.add(new DateWeek("周四","TH",5,false));
        list.add(new DateWeek("周五","FR",6,false));
        list.add(new DateWeek("周六","SA",7,false));
        list.add(new DateWeek("周日","SU",8,false));

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
    public List<HomeworkTypeBean> getHomeWorkTypes(int courseId, int grade) {
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

        List<HomeworkTypeBean> list = new ArrayList();
        HomeworkTypeBean homeWork = new HomeworkTypeBean();
        homeWork.name = "课堂作业本";
        homeWork.typeId = 0;
        homeWork.courseId = courseId;
        homeWork.bgResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_1) ;
        homeWork.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId) ;
        list.add(homeWork);

        HomeworkTypeBean homeWork1 = new HomeworkTypeBean();
        homeWork1.name = "课外作业本";
        homeWork1.typeId = 1;
        homeWork1.courseId = courseId;
        homeWork1.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_2);
        homeWork1.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId);
        list.add(homeWork1);

        HomeworkTypeBean homeWork2 = new HomeworkTypeBean();
        homeWork2.name = "课堂题卷本";
        homeWork2.typeId = 2;
        homeWork2.state=2;
        homeWork2.courseId = courseId;
        homeWork2.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_3);
        homeWork2.contentResId = ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),resId);
        list.add(homeWork2);

        if (courseId==0||courseId==2) {
            HomeworkTypeBean homeWork4 = new HomeworkTypeBean();
            homeWork4.name = "课文朗读册";
            homeWork4.typeId = 4;
            homeWork4.state=1;
            homeWork4.courseId = courseId;
            homeWork4.bgResId =ToolUtils.getImageResStr(MyApplication.Companion.getMContext(),R.mipmap.icon_homework_cover_4);
            list.add(homeWork4);
        }

        return list;

    }

    //语文作业本
    public List<Module> getYw(int grade) {
        List<Module> list = new ArrayList<>();
        if (grade <= 3) {
            Module module = new Module();
            module.name = "拼音田字本";
            module.resId = R.mipmap.icon_homework_yw_pytzb_1;
            module.resContentId = R.mipmap.icon_homework_yw_pytzb;

            Module module1 = new Module();
            module1.name = "田字本";
            module1.resId = R.mipmap.icon_homework_yw_tzb_1;
            module1.resContentId = R.mipmap.icon_homework_yw_tzb;

            Module module2 = new Module();
            module2.name = "拼音本";
            module2.resId = R.mipmap.icon_homework_yw_pyb_1;
            module2.resContentId = R.mipmap.icon_homework_yw_pyb;

            Module module3 = new Module();
            module3.name = "写字本";
            module3.resId = R.mipmap.icon_homework_yw_xzb_1;
            module3.resContentId = R.mipmap.icon_homework_yw_xzb;

            list.add(module);
            list.add(module1);
            list.add(module2);
            list.add(module3);
        } else {
            Module module = new Module();
            module.name = "中学练习本";
            module.resId = R.mipmap.icon_homework_other_lxb_1;
            module.resContentId = R.mipmap.icon_homework_other_lxb;

            Module module1 = new Module();
            module1.name = "中学作文本";
            module1.resId = R.mipmap.icon_homework_yw_zxzyb_1;
            module1.resContentId = R.mipmap.icon_homework_yw_zxzyb;

            list.add(module);
            list.add(module1);
        }
        return list;
    }

    //数学作业本
    public List<Module> getSx(int grade) {
        List<Module> list = new ArrayList<>();
        if (grade <= 3) {
            Module module = new Module();
            module.name = "空白";
            module.resId = R.drawable.bg_black_stroke_5dp_corner;
            module.resContentId = 0;

            Module module1 = new Module();
            module1.name = "小学数学本";
            module1.resId = R.mipmap.icon_homework_sx_sxb_1;
            module1.resContentId = R.mipmap.icon_homework_sx_sxb;

            list.add(module);
            list.add(module1);

        } else {
            Module module = new Module();
            module.name = "空白";
            module.resId = R.drawable.bg_black_stroke_5dp_corner;
            module.resContentId = 0;

            Module module1 = new Module();
            module1.name = "中学练习本";
            module1.resId = R.mipmap.icon_homework_other_lxb_1;
            module1.resContentId = R.mipmap.icon_homework_other_lxb;

            list.add(module);
            list.add(module1);
        }
        return list;
    }

    //英语作业本
    public List<Module> getYy(int grade) {
        List<Module> list = new ArrayList<>();
        if (grade <= 3) {
            Module module = new Module();
            module.name = "空白";
            module.resId = R.drawable.bg_black_stroke_5dp_corner;
            module.resContentId = 0;

            Module module1 = new Module();
            module1.name = "小学英语本";
            module1.resId = R.mipmap.icon_homework_yy_xxyyb_1;
            module1.resContentId = R.mipmap.icon_homework_yy_xxyyb;

            list.add(module);
            list.add(module1);

        } else {
            Module module = new Module();
            module.name = "中学英语本";
            module.resId = R.mipmap.icon_homework_yy_zxyyb_1;
            module.resContentId = R.mipmap.icon_homework_yy_zxyyb;

            Module module1 = new Module();
            module1.name = "中学练习本";
            module1.resId = R.mipmap.icon_homework_other_lxb_1;
            module1.resContentId = R.mipmap.icon_homework_other_lxb;

            list.add(module);
            list.add(module1);
        }
        return list;
    }

    //其他本子
    public List<Module> getOther() {
        List<Module> list = new ArrayList<>();
        Module module = new Module();
        module.name = "空白";
        module.resId = R.drawable.bg_black_stroke_5dp_corner;
        module.resContentId = 0;

        Module module1 = new Module();
        module1.name = "中学练习本";
        module1.resId = R.mipmap.icon_homework_other_lxb_1;
        module1.resContentId = R.mipmap.icon_homework_other_lxb;

        list.add(module);
        list.add(module1);
        return list;
    }

    //封面
    public List<Module> getHomeworkCover() {
        List<Module> list = new ArrayList<>();
        Module module = new Module();
        module.resId = R.mipmap.icon_homework_cover_1;

        Module module1 = new Module();
        module1.resId = R.mipmap.icon_homework_cover_2;

        Module module2 = new Module();
        module2.resId = R.mipmap.icon_homework_cover_3;

        Module module3 = new Module();
        module3.resId = R.mipmap.icon_homework_cover_4;

        list.add(module);
        list.add(module1);
        list.add(module2);
        list.add(module3);
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
    public List<Module> getNoteModuleDiary() {
        List<Module> list = new ArrayList<>();
        Module module = new Module();
        module.name = "横格本";
        module.resId = R.mipmap.icon_note_module_bg_1;
        module.resContentId =  R.mipmap.icon_note_details_bg_6;

        Module module1 = new Module();
        module1.name = "方格本";
        module1.resId = R.mipmap.icon_note_module_bg_2;
        module1.resContentId = R.mipmap.icon_note_details_bg_7;

        list.add(module);
        list.add(module1);
        return list;
    }

    //笔记本内容选择
    public List<Module> getNoteModuleBook() {
        List<Module> list = new ArrayList<>();
        Module module = new Module();
        module.name = "空白本";
        module.resId = R.drawable.bg_gray_stroke_10dp_corner;
        module.resContentId =  0;

        Module module1 = new Module();
        module1.name = "横格本";
        module1.resId = R.mipmap.icon_note_module_bg_1;
        module1.resContentId = R.mipmap.icon_note_details_bg_1;

        Module module2 = new Module();
        module2.name = "方格本";
        module2.resId = R.mipmap.icon_note_module_bg_2;
        module2.resContentId = R.mipmap.icon_note_details_bg_2;

        Module module3 = new Module();
        module3.name = "英语本";
        module3.resId = R.mipmap.icon_note_module_bg_3;
        module3.resContentId = R.mipmap.icon_note_details_bg_3;

        Module module4 = new Module();
        module4.name = "田字本";
        module4.resId = R.mipmap.icon_note_module_bg_4;
        module4.resContentId = R.mipmap.icon_note_details_bg_4;

        Module module5 = new Module();
        module5.name = "五线谱";
        module5.resId = R.mipmap.icon_note_module_bg_5;
        module5.resContentId = R.mipmap.icon_note_details_bg_5;

        list.add(module);
        list.add(module1);
        list.add(module2);
        list.add(module3);
        list.add(module4);
        list.add(module5);
        return list;
    }

}
