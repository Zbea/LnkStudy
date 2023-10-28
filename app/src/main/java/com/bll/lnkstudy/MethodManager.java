package com.bll.lnkstudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bll.lnkstudy.manager.AppDaoManager;
import com.bll.lnkstudy.manager.BookGreenDaoManager;
import com.bll.lnkstudy.manager.NoteDaoManager;
import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.mvp.model.painting.PaintingTypeBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.ui.activity.AccountLoginActivity;
import com.bll.lnkstudy.ui.activity.RecordListActivity;
import com.bll.lnkstudy.ui.activity.drawing.BookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaperDrawingActivity;
import com.bll.lnkstudy.utils.ActivityManager;
import com.bll.lnkstudy.utils.AppUtils;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class MethodManager {

    /**
     * 退出登录
     * @param context
     */
    public static void logout(Context context){
        SPUtil.INSTANCE.putString("token", "");
        SPUtil.INSTANCE.removeObj("user");

        Intent i=new Intent(context, AccountLoginActivity.class);
        i.putExtra("android.intent.extra.LAUNCH_SCREEN", 3);
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
     * @param context
     * @param bookBean
     */
    public static void gotoBookDetails(Context context, BookBean bookBean,int screenPos)  {
        AppUtils.stopApp(context,"com.geniatech.knote.reader");

        bookBean.isLook=true;
        bookBean.time=System.currentTimeMillis();
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean);
        EventBus.getDefault().post(Constants.BOOK_EVENT);

        List<AppBean> toolApps= AppDaoManager.getInstance().queryAll();
        JSONArray result =new JSONArray();
        for (AppBean item :toolApps) {
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
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage("com.geniatech.knote.reader");
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id",bookBean.bookId+"");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool",result.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", screenPos==3? 1 : screenPos);
        context.startActivity(intent);
    }

    /**
     * 跳转课本详情
     */
    public static void gotoTextBookDetails(Context context,int bookId){
        ActivityManager.getInstance().checkBookIDisExist(bookId);
        Intent intent=new Intent(context, BookDetailsActivity.class);
        intent.putExtra("book_id",bookId);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 跳转作业书
     */
    public static void gotoHomeworkBookDetails(Context context, HomeworkTypeBean typeBean){
        ActivityManager.getInstance().checkHomeworkBookIsExist(typeBean);
        Intent intent= new Intent(context, HomeworkBookDetailsActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("homework",typeBean);
        intent.putExtra("homeworkBundle",bundle);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 转跳作业本
     * @param context
     * @param item 作业分类
     * @param page 调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoHomeworkDrawing(Context context,HomeworkTypeBean item,int page){
        ActivityManager.getInstance().checkHomeworkDrawingIsExist(item);
        Intent intent= new Intent(context, HomeworkDrawingActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("homework",item);
        intent.putExtra("homeworkBundle",bundle);
        intent.putExtra("page",page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 转跳作业作业卷
     * @param context
     * @param item 作业分类
     * @param page 调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoHomeworkReelDrawing(Context context,HomeworkTypeBean item,int page){
        ActivityManager.getInstance().checkHomeworkPaperDrawingIsExist(item);
        Intent intent= new Intent(context, HomeworkPaperDrawingActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("homework",item);
        intent.putExtra("homeworkBundle",bundle);
        intent.putExtra("page",page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 跳转录音作业本
     */
    public static void gotoHomeworkRecord(Context context,HomeworkTypeBean item){
        Intent intent= new Intent(context, RecordListActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("homework",item);
        intent.putExtra("homeworkBundle",bundle);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 转跳考卷
     * @param context
     * @param page 调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoPaperDrawing(Context context, String course,int typeId, int page){
        ActivityManager.getInstance().checkPaperDrawingIsExist(course,typeId);
        Intent intent= new Intent(context, PaperDrawingActivity.class);
        intent.putExtra("course",course);
        intent.putExtra("typeId",typeId);
        intent.putExtra("page",page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 转跳笔记
     * @param context
     * @param screen 指定笔记都在右屏打开
     * @param page 调转指定页码（正常-1跳转最后一页）
     */
    public static void gotoNoteDrawing(Context context, Note note,int screen, int page){
        note.date=System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);

        Intent intent= new Intent(context, NoteDrawingActivity.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("note",note);
        intent.putExtra("noteBundle",bundle);
        intent.putExtra("page",page);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        if (screen!=0)
            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", screen);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 转跳本地画本、书法
     */
    public static void gotoPaintingDrawing(Context context, PaintingTypeBean item, int type){
        ActivityManager.getInstance().checkPaintingDrawingIsExist(type);
        Intent intent=new Intent(context, PaintingDrawingActivity.class);
        intent.setFlags(type);
        Bundle bundle=new Bundle();
        bundle.putSerializable("painting",item);
        intent.putExtra("paintingBundle",bundle);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 获取工具app
     * @param context
     * @param type 0 全部应用 1 设置为工具应用
     * @return
     */
    public static List<AppBean> getAppTools(Context context,int type){
        List<AppBean> apps;
        if (type==0){
            apps=AppDaoManager.getInstance().queryAll();
        }
        else {
            apps=AppDaoManager.getInstance().queryToolAll();
        }
        //从数据库中拿到应用集合 遍历查询已存储的应用是否已经卸载 卸载删除
        Iterator it=apps.iterator();
        while (it.hasNext()){
            AppBean item= (AppBean) it.next();
            if (!AppUtils.isAvailable(context,item.packageName)){
                it.remove();
                AppDaoManager.getInstance().deleteBean(item);
            }
        }
        return apps;
    }
    
}
