package com.bll.lnkstudy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.bll.lnkstudy.manager.AppDaoManager;
import com.bll.lnkstudy.manager.BookGreenDaoManager;
import com.bll.lnkstudy.manager.NoteDaoManager;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ItemList;
import com.bll.lnkstudy.mvp.model.ItemTypeBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolItemBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionTimeBean;
import com.bll.lnkstudy.mvp.model.PrivacyPassword;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.textbook.TextbookBean;
import com.bll.lnkstudy.ui.activity.AccountLoginActivity;
import com.bll.lnkstudy.mvp.model.CourseItem;
import com.bll.lnkstudy.ui.activity.RecordListActivity;
import com.bll.lnkstudy.ui.activity.drawing.CalligraphyDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.TestpaperDrawingActivity;
import com.bll.lnkstudy.utils.ActivityManager;
import com.bll.lnkstudy.utils.AppUtils;
import com.bll.lnkstudy.utils.DateUtils;
import com.bll.lnkstudy.utils.FileUtils;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.SToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MethodManager {

    private static User user = SPUtil.INSTANCE.getObj("user", User.class);

    public static void getUser() {
        user = SPUtil.INSTANCE.getObj("user", User.class);
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
    public static void saveCourses(List<CourseItem> courseItems) {
        SPUtil.INSTANCE.putCourseItems("courseItems", courseItems);
        EventBus.getDefault().post(Constants.COURSEITEM_EVENT);
    }

    /**
     * 获取学生科目
     */
    public static List<CourseItem> getCourses() {
        return SPUtil.INSTANCE.getCourseItems("courseItems");
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

    /**
     * 跳转阅读器
     *
     * @param context
     * @param bookBean
     * key_book_type 0普通书籍 1pdf书籍 2pdf课本 3文档
     */
    public static void gotoBookDetails(Context context, BookBean bookBean) {

        if (!MethodManager.getSchoolPermissionAllow(0)) {
            SToast.showText(1, "学校该时间不允许查看书籍");
            return;
        }

        if (!MethodManager.getParentPermissionAllow(0)) {
            SToast.showText(1, "家长该时间不允许查看书籍");
            return;
        }

        AppUtils.stopApp(context, Constants.PACKAGE_READER);
        User user = SPUtil.INSTANCE.getObj("user", User.class);

        bookBean.isLook = true;
        bookBean.time = System.currentTimeMillis();
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean);
        EventBus.getDefault().post(Constants.BOOK_EVENT);

        List<AppBean> toolApps = getAppTools(context, 1);
        JSONArray result = new JSONArray();
        for (AppBean item : toolApps) {
            if (Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY))
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
        String format = MethodManager.getUrlFormat(bookBean.bookPath);
        int key_type = 0;
        if (format.contains("pdf")) {
            key_type = 1;
        } else {
            key_type = 0;
        }
        Intent intent = new Intent();
        intent.setAction("com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id", bookBean.bookId + "");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool", result.toString());
        intent.putExtra("userId", user.accountId);
        intent.putExtra("type", 1);
        intent.putExtra("drawPath", bookBean.bookDrawPath);
        intent.putExtra("key_book_type", key_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转课本详情
     */
    public static void gotoTextBookDetails(Context context, TextbookBean textbookBean) {

        AppUtils.stopApp(context, Constants.PACKAGE_READER);
        User user = SPUtil.INSTANCE.getObj("user", User.class);

        List<AppBean> toolApps = getAppTools(context, 1);
        JSONArray result = new JSONArray();
        for (AppBean item : toolApps) {
            if (Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY))
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

        Intent intent = new Intent();
        intent.setAction("com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", textbookBean.bookPath);
        intent.putExtra("key_book_id", textbookBean.bookId + "");
        intent.putExtra("bookName", textbookBean.bookName);
        intent.putExtra("tool", result.toString());
        intent.putExtra("userId", user.accountId);
        intent.putExtra("type", 2);
        intent.putExtra("drawPath", textbookBean.bookDrawPath);
        intent.putExtra("key_book_type", 2);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * 跳转作业书
     */
    public static void gotoHomeworkBookDetails(Context context, HomeworkTypeBean typeBean) {
        ActivityManager.getInstance().checkHomeworkBookIsExist(typeBean);
        Intent intent = new Intent(context, HomeworkBookDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", typeBean);
        intent.putExtra("homeworkBundle", bundle);
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
        context.startActivity(intent);
    }

    /**
     * 转跳作业本
     *
     * @param context
     * @param item    作业分类
     * @param page    调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoHomeworkDrawing(Context context, HomeworkTypeBean item, int page) {
        ActivityManager.getInstance().checkHomeworkDrawingIsExist(item);
        Intent intent = new Intent(context, HomeworkDrawingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", item);
        intent.putExtra("homeworkBundle", bundle);
        intent.putExtra("page", page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
        context.startActivity(intent);
    }


    /**
     * 转跳作业作业卷
     *
     * @param context
     * @param item    作业分类
     * @param page    调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoHomeworkReelDrawing(Context context, HomeworkTypeBean item, int page) {
        ActivityManager.getInstance().checkHomeworkPaperDrawingIsExist(item);
        Intent intent = new Intent(context, HomeworkPaperDrawingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", item);
        intent.putExtra("homeworkBundle", bundle);
        intent.putExtra("page", page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
        context.startActivity(intent);
    }

    /**
     * 跳转录音作业本
     */
    public static void gotoHomeworkRecord(Context context, HomeworkTypeBean item) {
        Intent intent = new Intent(context, RecordListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("homework", item);
        intent.putExtra("homeworkBundle", bundle);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
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
        Intent intent = new Intent(context, TestpaperDrawingActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("typeId", typeId);
        intent.putExtra("page", page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
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
        note.date = System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);

        Intent intent = new Intent(context, NoteDrawingActivity.class);
        intent.putExtra("noteId", note.id);
        intent.putExtra("page", page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
        if (screen != 0)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, screen);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 转跳本地画本、书法
     */
    public static void gotoPaintingDrawing(Context context, ItemTypeBean item, int type) {
        if (!MethodManager.getSchoolPermissionAllow(2)) {
            SToast.showText(2, "学校该时间不允许手绘");
            return;
        }

        if (type == 3) {
            ActivityManager.getInstance().checkPaintingDrawingIsExist();
            Intent intent = new Intent(context, PaintingDrawingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("painting", item);
            intent.putExtra("paintingBundle", bundle);
            intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
            context.startActivity(intent);
        } else {
            ActivityManager.getInstance().checkCalligraphyDrawingIsExist();
            Intent intent = new Intent(context, CalligraphyDrawingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("calligraphy", item);
            intent.putExtra("paintingBundle", bundle);
            intent.putExtra("android.intent.extra.KEEP_FOCUS", true);
            context.startActivity(intent);
        }

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
            if (!AppUtils.isAvailable(context, item.packageName) && !Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY)) {
                it.remove();
                AppDaoManager.getInstance().deleteBean(item);
            }
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
                    PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
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
            SPUtil.INSTANCE.putObj("privacyPasswordDiary", privacyPassword);
        } else {
            SPUtil.INSTANCE.putObj("privacyPasswordNote", privacyPassword);
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
            return SPUtil.INSTANCE.getObj("privacyPasswordDiary", PrivacyPassword.class);
        } else {
            return SPUtil.INSTANCE.getObj("privacyPasswordNote", PrivacyPassword.class);
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
        PermissionParentBean permissionParentBean = SPUtil.INSTANCE.getObj("parentPermission", PermissionParentBean.class);
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
        List<Integer> weeks = new ArrayList();
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
        PermissionSchoolItemBean item = SPUtil.INSTANCE.getObj("schoolPermission", PermissionSchoolItemBean.class);
        if (item == null) {
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
        boolean isAllow = true;
        long currentTime = DateUtils.getCurrentHourInMillis();
        int week = DateUtils.getWeek(System.currentTimeMillis());
        if (item.weeks.contains(week)) {
            for (int j = 0; j < item.limitTime.size(); j++) {
                PermissionSchoolItemBean.TimeBean timeBean = item.limitTime.get(j);
                long startTime = DateUtils.date4StampToHour(timeBean.startTime);
                long endTime = DateUtils.date4StampToHour(timeBean.endTime);
                if (currentTime >= startTime && currentTime <= endTime) {
                    isAllow = false;
                    break;
                }
            }
        }
        return isAllow;
    }

    /**
     * 获取对应科目的考试分类id
     *
     * @param subject
     * @return
     */
    public static int getExamTypeId(String subject) {
        int i = DataBeanManager.INSTANCE.getCourseId(subject) + user.grade;
        String idStr = i + String.valueOf(user.accountId);
        return Integer.parseInt(idStr);
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
     * 获取url的格式后缀
     *
     * @param url
     * @return
     */
    public static String getUrlFormat(String url) {
        return url.substring(url.lastIndexOf("."));
    }

    /**
     * 获取分数
     * @param scoreStr
     * @return
     */
    public static int getScore(String scoreStr){
        if (scoreStr==null||scoreStr.isEmpty()||!TextUtils.isDigitsOnly(scoreStr)){
            return 0;
        }
        else {
            return Integer.valueOf(scoreStr);
        }
    }

}
