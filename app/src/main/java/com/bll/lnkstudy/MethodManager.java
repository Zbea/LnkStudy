package com.bll.lnkstudy;

import static com.bll.lnkstudy.Constants.BOOK_EVENT;
import static com.bll.lnkstudy.Constants.SP_PARENT_PERMISSION;
import static com.bll.lnkstudy.Constants.SP_SCHOOL_PERMISSION;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bll.lnkstudy.dialog.ImageDialog;
import com.bll.lnkstudy.manager.AppDaoManager;
import com.bll.lnkstudy.manager.BookGreenDaoManager;
import com.bll.lnkstudy.manager.ItemTypeDaoManager;
import com.bll.lnkstudy.manager.NoteDaoManager;
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager;
import com.bll.lnkstudy.manager.TextbookGreenDaoManager;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.Area;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ItemList;
import com.bll.lnkstudy.mvp.model.ItemTypeBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList;
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolItemBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionTimeBean;
import com.bll.lnkstudy.mvp.model.PrivacyPassword;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.book.TextbookBean;
import com.bll.lnkstudy.ui.activity.account.AccountLoginActivity;
import com.bll.lnkstudy.ui.activity.PaintingImageActivity;
import com.bll.lnkstudy.ui.activity.book.DictionaryBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.homework.HomeworkRecordActivity;
import com.bll.lnkstudy.ui.activity.homework.HomeworkRecordListActivity;
import com.bll.lnkstudy.ui.activity.book.TextBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.CalligraphyDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.FileDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkShareDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.TestPaperDrawingActivity;
import com.bll.lnkstudy.utils.ActivityManager;
import com.bll.lnkstudy.utils.AppUtils;
import com.bll.lnkstudy.utils.DateUtils;
import com.bll.lnkstudy.utils.FileUtils;
import com.bll.lnkstudy.utils.MD5Utils;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.SToast;
import com.bll.lnkstudy.utils.date.Lunar;
import com.bll.lnkstudy.utils.date.LunarSolarConverter;
import com.bll.lnkstudy.utils.date.Solar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class MethodManager {

    public static User getUser(){
        return SPUtil.INSTANCE.getObj("user", User.class);
    }

    public static boolean isLogin(){
        String tokenStr=SPUtil.INSTANCE.getString("token");
        return !TextUtils.isEmpty(tokenStr) && getUser()!=null;
    }

    public static long getAccountId(){
        User user=getUser();
        if (user==null){
            return 0L;
        }
        else {
            return user.accountId;
        }
    }

    /**
     * 获取学生年级
     * @param classGroups
     */
    public static void setGradeByClassGroups(List<ClassGroup> classGroups){
        User mUser=getUser();
        int currentGrade = 0;
        int oldGrade = mUser.grade;
        for (ClassGroup item:classGroups) {
            if (item.state == 1) {
                currentGrade = item.grade;
                break;
            }
        }
        if (currentGrade > 0 &&currentGrade != oldGrade) {
            mUser.grade = currentGrade;
            SPUtil.INSTANCE.putObj("user",mUser);
            //年级变化后，刷新整个数据
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                            EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
                    ,500);
            //刷新作业本、测卷上传
            if (oldGrade>0){
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                EventBus.getDefault().post(Constants.USER_CHANGE_GRADE_EVENT)
                        ,1500);
            }
        }
    }

    /**
     * 获取当前日期处于那个学期农历一月初一到8月15日为下学期
     * @return
     */
    public static int getSemester(){
        long time=System.currentTimeMillis();

        Lunar lunar=new Lunar();
        lunar.isleap=DateUtils.isleap();
        lunar.lunarYear = DateUtils.getYear();
        lunar.lunarMonth = 1;
        lunar.lunarDay = 1;
        Solar solar = LunarSolarConverter.LunarToSolar(lunar);

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.YEAR, solar.solarYear);
        calendar.set(Calendar.MONTH, solar.solarMonth-1);
        calendar.set(Calendar.DAY_OF_MONTH, solar.solarDay);

        long startTime=calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, solar.solarYear);
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DAY_OF_MONTH, 15);

        long endTime=calendar.getTimeInMillis();

        if (time>=startTime&&time<=endTime){
            return 2;
        }
        else {
            return 1;
        }
    }

    /**
     * 获取老师是否允许查看(true允许查看)
     * @return
     */
    public static boolean isClassGroupPermissionArrow(){
        return System.currentTimeMillis()<DataBeanManager.INSTANCE.getPermissionTime();
    }

    /**
     * 保存公用数据
     */
    public static void saveItemLists(String key, List<ItemList> items) {
        SPUtil.INSTANCE.putListItems(key, items);
    }

    /**
     * 获取公用数据
     */
    public static List<ItemList> getItemLists(String key) {
        return SPUtil.INSTANCE.getListItems(key);
    }


    /**
     * 保存学生科目
     */
    public static void saveCourses(List<String> courseItems) {
        SPUtil.INSTANCE.putCurrentCourses("currentCourses", courseItems);
        EventBus.getDefault().post(Constants.COURSEITEM_EVENT);
    }

    /**
     * 获取学生科目
     */
    public static List<String> getCourses() {
        return SPUtil.INSTANCE.getCurrentCourses("currentCourses");
    }

    /**
     * 保存班群列表
     */
    public static void saveClassGroups(List<ClassGroup> classGroups) {
        SPUtil.INSTANCE.putClassGroupItems("classGroups", classGroups);
        EventBus.getDefault().post(Constants.COURSEITEM_EVENT);
    }

    /**
     * 获取班群列表
     */
    public static List<ClassGroup> getClassGroups() {
        return SPUtil.INSTANCE.getClassGroupItems("classGroups");
    }

    /**
     * 获取省
     * @param context
     * @return
     * @throws IOException
     */
    public static List<Area> getProvinces(Context context) throws IOException {
        String areaJson = FileUtils.readFileContent(context.getResources().getAssets().open("city.json"));
        return new Gson().fromJson(areaJson, new TypeToken<List<Area>>(){}.getType());
    }
    /**
     * 退出登录
     *
     * @param context
     */
    public static void logout(Context context) {
        SPUtil.INSTANCE.putString("token", "");
        SPUtil.INSTANCE.removeObj("user");

        Intent i = new Intent(context, AccountLoginActivity.class);
        i.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL);
        context.startActivity(i);
        ActivityManager.getInstance().finishOthers(AccountLoginActivity.class);

        //发出退出登录广播
        Intent intent = new Intent();
        intent.putExtra("token", "");
        intent.putExtra("userId", 0L);
        intent.setAction(Constants.LOGOUT_BROADCAST_EVENT);
        context.sendBroadcast(intent);
    }

    public static void gotoDocument(Context context,File file){
        String format=FileUtils.getUrlFormat(file.getPath());
        if (format.equals(".ppt") || format.equals(".pptx")){
            gotoPptDetails(context,file.getPath(),Constants.SCREEN_LEFT);
        }
        else if (format.equals(".png") || format.equals(".jpg")||format.equals(".jpeg")){
            List<String> images=new ArrayList<>();
            images.add(file.getPath());
            new ImageDialog(context,1,images).builder();
        }
        else {
            String fileName=FileUtils.getUrlName(file.getPath());
            String drawPath=file.getParent()+"/"+fileName+"draw/";
            Intent intent=new Intent();
            intent.setAction("com.geniatech.reader.action.VIEW_BOOK_PATH");
            intent.setPackage(Constants.PACKAGE_READER);
            intent.putExtra("path", file.getPath());
            intent.putExtra("bookName", fileName);
            intent.putExtra("tool", getJsonArray().toString());
            intent.putExtra("userId", getAccountId());
            intent.putExtra("type", 1);
            intent.putExtra("drawPath", drawPath);
            intent.putExtra("key_book_type", 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void gotoPptDetails(Context context,String path,int flags){
        if (AppUtils.isAvailable(context,Constants.PACKAGE_PPT)){
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(Constants.PACKAGE_PPT,"com.htfyun.dualdocreader.OpenFileActivity"));
            intent.putExtra("open_mode", 0);  // 0-本地解析打开, 1-微软在线预览
            intent.putExtra("path", path);
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, flags);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转阅读器
     *
     * @param context
     * @param bookBean
     * key_book_type 0普通书籍 1pdf书籍 2pdf课本 3文档
     */
    public static void gotoBookDetails(Context context, BookBean bookBean) {
        if (!isClassGroupPermissionArrow()){
            if (!MethodManager.getSchoolPermissionAllow(0)) {
                SToast.showText(1, "学校不允许该时间段查看书籍");
                return;
            }
            if (!MethodManager.getParentPermissionAllow(0)) {
                SToast.showText(1, "家长不允许该时间段查看书籍");
                return;
            }
            if (!DateUtils.isTimeBetween7And22()){
                SToast.showText(1, "该时间无法查看书籍");
                return;
            }
        }

        AppUtils.stopApp(context, Constants.PACKAGE_READER);

        bookBean.isLook = true;
        bookBean.time = System.currentTimeMillis();
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean);

        String format = FileUtils.getUrlFormat(bookBean.bookPath);
        int key_type = 0;
        if (format.contains("pdf")) {
            key_type = 1;
        }
        Intent intent = new Intent();
        intent.setAction("com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id", bookBean.bookId + "");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool", getJsonArray().toString());
        intent.putExtra("userId", getUser().accountId);
        intent.putExtra("type", 1);
        intent.putExtra("drawPath", bookBean.bookDrawPath);
        intent.putExtra("key_book_type", key_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->
                        EventBus.getDefault().post(Constants.BOOK_EVENT)
                ,3000);
    }

    private static JSONArray getJsonArray() {
        List<AppBean> toolApps = getAppTools(MyApplication.Companion.getMContext(), 1);
        JSONArray result = new JSONArray();
        for (AppBean item : toolApps) {
            if (Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY)||item.type==2)
                continue;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("appName", item.appName);
                jsonObject.put("packageName", item.packageName);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            result.put(jsonObject);
        }
        return result;
    }

    /**
     * 删除书籍
     * @param book
     */
    public static void deleteBook(BookBean book){
        BookGreenDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        if (new File(book.bookDrawPath).exists())
            FileUtils.deleteFile(new File(book.bookDrawPath));
        //删除增量更新
        DataUpdateManager.INSTANCE.deleteDateUpdate(6,book.bookId);
        EventBus.getDefault().post(BOOK_EVENT);
    }

    /**
     * 跳转课本详情
     */
    public static void gotoTextBookDetails(Context context, TextbookBean textbookBean) {
        ActivityManager.getInstance().checkTextBookIsExist(textbookBean);
        Intent intent = new Intent(context, TextBookDetailsActivity.class);
        intent.putExtra("bookId",textbookBean.bookId);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    /**
     * 删除课本
     * @param book
     */
    public static void deleteTextbook(TextbookBean book){
        TextbookGreenDaoManager.getInstance().deleteBook(book);
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        FileUtils.deleteFile(new File(book.bookDrawPath));
        String annotationPath=new FileAddress().getPathTextBookAnnotation(MD5Utils.digest(book.bookId+""));
        FileUtils.deleteFile(new File(annotationPath));
        //删除增量更新
        DataUpdateManager.INSTANCE.deleteDateUpdate(1,book.bookId);
    }

    /**
     * 跳转字典词典
     */
    public static void gotoDictionaryDetails(Context context, int bookId,int screen) {
        ActivityManager.getInstance().finishActivity(DictionaryBookDetailsActivity.class.getName());
        Intent intent = new Intent(context, DictionaryBookDetailsActivity.class);
        intent.putExtra("bookId",bookId);
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, screen);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    public static void setHomeworkTypeBundle(Intent intent, HomeworkTypeBean item){
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", item);
        intent.putExtra("homeworkBundle", bundle);
    }

    public static void setHomeworkTypeBundle(Intent intent, HomeworkTypeBean item, HomeworkMessageList.MessageBean messageBean){
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", item);
        if (messageBean!=null)
            bundle.putSerializable("messageBean",messageBean);
        intent.putExtra("homeworkBundle", bundle);
    }

    public static HomeworkTypeBean getHomeworkTypeBundle(Intent intent){
        Bundle bundle = intent.getBundleExtra("homeworkBundle");
        return (HomeworkTypeBean) bundle.getSerializable("homework");
    }

    public static HomeworkMessageList.MessageBean getHomeworkMessageBundle(Intent intent){
        Bundle bundle = intent.getBundleExtra("homeworkBundle");
        return (HomeworkMessageList.MessageBean) bundle.getSerializable("messageBean");
    }

    /**
     * 跳转作业书
     */
    public static void gotoHomeworkBookDetails(Context context, HomeworkTypeBean typeBean, HomeworkMessageList.MessageBean messageBean) {
        ActivityManager.getInstance().checkHomeworkBookIsExist(typeBean);
        Intent intent = new Intent(context, HomeworkBookDetailsActivity.class);
        setHomeworkTypeBundle(intent,typeBean,messageBean);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    /**
     * 转跳作业本
     *
     * @param context
     * @param item    作业分类
     * @param page    调转指定页码（正常-1跳转最后一页）
     * @param messageBean 选中作业下标
     */
    public static void gotoHomeworkDrawing(Context context, HomeworkTypeBean item,int page, HomeworkMessageList.MessageBean messageBean) {
        ActivityManager.getInstance().checkHomeworkDrawingIsExist(item);
        Intent intent = new Intent(context, HomeworkDrawingActivity.class);
        setHomeworkTypeBundle(intent,item,messageBean);
        intent.putExtra("page", page);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    /**
     * 转跳朗读本
     * @param context
     * @param item    作业分类
     * @param messageBean 选中作业下标
     */
    public static void gotoHomeworkRecord(Context context, HomeworkTypeBean item, HomeworkMessageList.MessageBean messageBean) {
        ActivityManager.getInstance().finishActivity(HomeworkRecordActivity.class.getName());
        Intent intent = new Intent(context, HomeworkRecordActivity.class);
        setHomeworkTypeBundle(intent,item,messageBean);
        context.startActivity(intent);
    }


    /**
     * 跳转录音作业本
     */
    public static void gotoHomeworkRecordList(Context context, HomeworkTypeBean item) {
        Intent intent = new Intent(context, HomeworkRecordListActivity.class);
        setHomeworkTypeBundle(intent,item);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 跳转分享作业本
     */
    public static void gotoHomeworkShareList(Context context, HomeworkTypeBean item) {
        Intent intent = new Intent(context, HomeworkShareDrawingActivity.class);
        setHomeworkTypeBundle(intent,item);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    /**
     * 转跳作业作业卷
     *
     * @param context
     * @param item    作业分类
     * @param page
     */
    public static void gotoHomeworkReelDrawing(Context context, HomeworkTypeBean item,int page,HomeworkMessageList.MessageBean messageBean) {
        ActivityManager.getInstance().checkHomeworkPaperDrawingIsExist(item);
        Intent intent = new Intent(context, HomeworkPaperDrawingActivity.class);
        setHomeworkTypeBundle(intent,item,messageBean);
        intent.putExtra("page", page);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }


    /**
     * 转跳考卷
     *
     * @param context
     * @param page    调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoPaperDrawing(Context context, String course, int typeId, int page) {
        ActivityManager.getInstance().checkPaperDrawingIsExist(course, typeId);
        Intent intent = new Intent(context, TestPaperDrawingActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("typeId", typeId);
        intent.putExtra("page", page);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        context.startActivity(intent);
    }

    /**
     * 转跳笔记
     *
     * @param context
     * @param screen  指定笔记都在右屏打开
     * @param page    调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoNoteDrawing(Context context, Note note, int screen, int page) {
        Intent intent = new Intent(context, NoteDrawingActivity.class);
        intent.putExtra("noteId", note.id);
        intent.putExtra("page", page);
        intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
        if (screen != 0)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, screen);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);

        note.date = System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);
    }

    /**
     * 转跳本地画本、书法
     */
    public static void gotoPaintingDrawing(Context context, int type, int cloudId) {
        if (!isClassGroupPermissionArrow()){
            if (!MethodManager.getSchoolPermissionAllow(2)) {
                SToast.showText(2, "学校不允许该时间段手绘");
                return;
            }
            if (!DateUtils.isTimeBetween7And22()){
                SToast.showText(2, "该时间段无法手绘");
                return;
            }
        }
        if (type == 0) {
            ActivityManager.getInstance().checkPaintingDrawingIsExist();
            Intent intent = new Intent(context, PaintingDrawingActivity.class);
            intent.setFlags(cloudId);
            intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
            context.startActivity(intent);
        } else {
            ActivityManager.getInstance().checkCalligraphyDrawingIsExist();
            Intent intent = new Intent(context, CalligraphyDrawingActivity.class);
            intent.setFlags(cloudId);
            intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转书画详情
     * @param context
     * @param contentId
     * @param screen
     */
    public static void gotoPaintingImage(Context context,int contentId,int screen){
        Intent intent = new Intent(context, PaintingImageActivity.class);
        intent.setFlags(contentId);
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, screen);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }


    /**
     * 删除本地画本、书法
     * @param item
     */
    public static void deletePaintingDrawing(int type,ItemTypeBean item) {
        String path="";
        int typeId=0;
        if (item!=null){
            ItemTypeDaoManager.getInstance().deleteBean(item);
            path=item.path;
            typeId=item.typeId;
        }
        else {
            path= new FileAddress().getPathPaintingDraw(type,0);
        }
        PaintingDrawingDaoManager.getInstance().deleteBean(type,typeId);
        FileUtils.deleteFile(new File(path));
        DataUpdateManager.INSTANCE.deleteDateUpdate(5, typeId);
    }

    /**
     * 跳转截图列表
     * @param context
     * @param index
     * @param tabPath
     */
    public static void gotoScreenFile(Context context,int index,String tabPath){
        Intent intent=new Intent(context,FileDrawingActivity.class);
        intent.putExtra("pageIndex",index);
        intent.putExtra("pagePath",tabPath);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 获取工具app
     *
     * @param context
     * @param type    0 全部应用 1 设置为工具应用
     * @return
     */
    public static List<AppBean> getAppTools(Context context, int type) {
        List<AppBean> apps;
        if (type == 0) {
            apps = AppDaoManager.getInstance().queryAll();
        } else {
            apps = AppDaoManager.getInstance().queryToolAll();
        }
        //从数据库中拿到应用集合 遍历查询已存储的应用是否已经卸载 卸载删除
        Iterator<AppBean> it = apps.iterator();
        while (it.hasNext()) {
            AppBean item = it.next();
            if (!AppUtils.isAvailable(context, item.packageName) && !Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY)&&item.type!=2) {
                it.remove();
                AppDaoManager.getInstance().deleteBean(item);
            }
            item.isCheck=false;
        }
        return apps;
    }


    /**
     * 打开屏幕
     *
     * @param context
     */
    @SuppressLint("InvalidWakeLockTag")
    public static void wakeUpScreen(Context context) {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock( PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP , "Loneworker - FULL WAKE LOCK");
                    fullWakeLock.acquire(5 * 60 * 1000L);
                    if (fullWakeLock.isHeld())
                        fullWakeLock.release();
                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        }.execute();
    }

    /**
     * 保存私密密码
     * type 0日记1密本
     *
     * @param privacyPassword
     */
    public static void savePrivacyPassword(int type, PrivacyPassword privacyPassword) {
        if (type == 0) {
            SPUtil.INSTANCE.putObj(Constants.SP_PRIVACY_PW_DIARY, privacyPassword);
        } else {
            SPUtil.INSTANCE.putObj(Constants.SP_PRIVACY_PW_NOTE, privacyPassword);
        }
    }

    /**
     * 获取私密密码
     * type 0日记1密本
     *
     * @return
     */
    public static PrivacyPassword getPrivacyPassword(int type) {
        if (type == 0) {
            return SPUtil.INSTANCE.getObj(Constants.SP_PRIVACY_PW_DIARY, PrivacyPassword.class);
        } else {
            return SPUtil.INSTANCE.getObj(Constants.SP_PRIVACY_PW_NOTE, PrivacyPassword.class);
        }
    }

    /**
     * 获取家长是否允许学生查看 返回true可以查看 返回false不能查看
     *
     * @param type 0书架 1义教
     * @return
     */
    public static boolean getParentPermissionAllow(int type) {
        boolean isAllow = true;
        long currentTime = DateUtils.getCurrentHourInMillis();
        int week = DateUtils.getWeek(System.currentTimeMillis());
        PermissionParentBean permissionParentBean = SPUtil.INSTANCE.getObj(SP_PARENT_PERMISSION, PermissionParentBean.class);
        if (permissionParentBean == null) {
            return true;
        }
        if (type == 0) {
            if (permissionParentBean.isAllowBook) {
                if (permissionParentBean.bookList.isEmpty()) {
                    return true;
                } else {
                    for (int i = 0; i < permissionParentBean.bookList.size(); i++) {
                        PermissionTimeBean permissionTimeBean = permissionParentBean.bookList.get(i);
                        List<Integer> weeks = getStringArrayToIntArray(permissionTimeBean.weeks.split(","));
                        if (weeks.contains(week)) {
                            isAllow = currentTime >= permissionTimeBean.endTime || currentTime <= permissionTimeBean.startTime;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            if (permissionParentBean.isAllowVideo) {
                if (permissionParentBean.videoList.isEmpty()) {
                    return true;
                } else {
                    for (int i = 0; i < permissionParentBean.videoList.size(); i++) {
                        PermissionTimeBean permissionTimeBean = permissionParentBean.videoList.get(i);
                        List<Integer> weeks = getStringArrayToIntArray(permissionTimeBean.weeks.split(","));
                        if (weeks.contains(week)) {
                            isAllow = currentTime >= permissionTimeBean.endTime || currentTime <= permissionTimeBean.startTime;
                        }
                    }
                }
            } else {
                return false;
            }
        }
        return isAllow;
    }

    /**
     * 字符串数组 转 list数字集合
     *
     * @param strings
     * @return
     */
    private static List<Integer> getStringArrayToIntArray(String[] strings) {
        List<Integer> weeks = new ArrayList<>();
        for (String str : strings) {
            weeks.add(Integer.valueOf(str));
        }
        return weeks;
    }

    /**
     * 获取学生是否允许学生查看 返回false不可以查看 返回true可以查看
     *
     * @param type 0书架 1义教 2书画
     * @return
     */
    public static boolean getSchoolPermissionAllow(int type) {
        PermissionSchoolItemBean item = SPUtil.INSTANCE.getObj(SP_SCHOOL_PERMISSION, PermissionSchoolItemBean.class);
        if (item == null) {
            return true;
        }
        if (!item.enable){
            return true;
        }
        if (type == 0) {
            if (item.isAllowBook) {
                return isExistCurrentTime(item);
            } else {
                return true;
            }
        } else if (type == 1) {
            if (item.isAllowVideo) {
                return isExistCurrentTime(item);
            } else {
                return true;
            }
        } else {
            if (item.isAllowPainting) {
                return isExistCurrentTime(item);
            } else {
                return true;
            }
        }
    }

    private static boolean isExistCurrentTime(PermissionSchoolItemBean item) {
        String dayStr=DateUtils.longToStringDataNoHour(DateUtils.getStartOfDayInMillis());
        if (item.dateMap!=null&&item.dateMap.containsKey(dayStr)){
            return true;
        }
        boolean isAllow = true;
        long currentTime = DateUtils.getCurrentHourInMillis();
        int week = DateUtils.getWeek(System.currentTimeMillis());
        if (item.weeks.contains(week)) {
            for (int j = 0; j < item.limitTime.size(); j++) {
                PermissionSchoolItemBean.TimeBean timeBean = item.limitTime.get(j);
                long startTime = DateUtils.getTimeHourInMillis(timeBean.startTime);
                long endTime = DateUtils.getTimeHourInMillis(timeBean.endTime);
                if (currentTime >= startTime && currentTime <= endTime) {
                    isAllow = false;
                    break;
                }
            }
        }
        return isAllow;
    }

    /**
     * 获取固定生成测试卷id
     * @param name
     * @param subject
     * @return
     */
    public static int getTestPaperAutoTypeId(String name,String subject){
        User mUser=getUser();
        int type=DataBeanManager.INSTANCE.getAutoTestPaperTypes().indexOf(name)+1;
        String idStr =String.valueOf(type)+DataBeanManager.INSTANCE.getCourseId(subject) +mUser.grade;
        return Integer.parseInt(idStr);
    }

    /**
     * (本地默认创建)获取对应分类，对应作业的分类id
     * @param subject
     * @param subType
     * @return
     */
    public static int getHomeworkTypeId(String subject,int subType){
        User mUser=getUser();
        String idStr = String.valueOf(subType)+0+DataBeanManager.INSTANCE.getCourseId(subject) + mUser.grade;
        return Integer.parseInt(idStr);
    }
    /**
     * (老师默认创建)获取对应分类，对应作业的分类id
     */
    public static int getHomeworkAutoTypeId(String name,String subject){
        User mUser=getUser();
        int type=DataBeanManager.INSTANCE.getAutoHomeworkTypes().indexOf(name)+1;
        String idStr =String.valueOf(type)+DataBeanManager.INSTANCE.getCourseId(subject) + mUser.grade;
        return Integer.parseInt(idStr);
    }

    /**
     * 家长作业本本地typeID
     * @param typeId
     * @return
     */
    public static int getParentHomeworkTypeId(int typeId){
        User mUser=getUser();
        String idStr = String.valueOf(typeId) + mUser.grade;
        return Integer.parseInt(idStr);
    }

    /**
     * 家长作业本本地typeId转成线上typeId
     * @param typeId
     * @return
     */
    public static int getParentHomeworkTypeIdOld(int typeId){
        User mUser=getUser();
        int index= (int) Math.pow(10,String.valueOf(mUser.grade).length());
        return typeId/index;
    }

    /**
     * 获取状态栏的值
     *
     * @return
     */
    public static int getStatusBarValue() {
        return Settings.System.getInt(MyApplication.Companion.getMContext().getContentResolver(), "statusbar_hide_time", 0);
    }

    /**
     * 设置状态栏的值
     *
     * @return
     */
    public static void setStatusBarValue(int value) {
        Settings.System.putInt(MyApplication.Companion.getMContext().getContentResolver(), "statusbar_hide_time", value);
    }

    /**
     * 加载不失真背景
     * @param context
     * @param resId
     * @param imageView
     */
    public static void setImageResource(Context context, int resId, ImageView imageView){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 防止自动缩放
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 加载本地图片
     * @param path
     * @param imageView
     */
    public static void setImageFile(String path, ImageView imageView){
        File file=new File(path);
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化不选中 指定位置选中
     * @param list
     * @param position
     * @return
     */
    public static List<ItemTypeBean> setItemTypeBeanCheck(List<ItemTypeBean> list,int position){
        if (list.size()>position){
            for (ItemTypeBean item:list) {
                item.isCheck=false;
            }
            list.get(position).isCheck=true;
        }
        return list;
    }

    /**
     * 获取科目
     * @return
     * @throws IOException
     */
    public static List<ItemList> getSubjects() throws IOException {
        String areaJson = FileUtils.readFileContent(MyApplication.Companion.getMContext().getResources().getAssets().open("subject.json"));
        return new Gson().fromJson(areaJson, new TypeToken<List<ItemList>>(){}.getType());
    }

    /**
     * 批改通知获取得分字符显示
     * @param score
     * @param question
     * @param questionType
     * @return
     */
    public static String getCorrectNoticeScore(double score,String question,int questionType){
        if (question==null){
            return "";
        }
        if (question.isEmpty()){
            return DataBeanManager.INSTANCE.getScoreStandardStr(score,questionType);
        }
        else if (question.length()<20){
            return DataBeanManager.INSTANCE.getResultStandardStr(score,questionType);
        }
        else{
            return String.valueOf(score);
        }
    }
}
