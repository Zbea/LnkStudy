package com.bll.lnkstudy.utils;

import android.app.Activity;

import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.ui.activity.HomeLeftActivity;
import com.bll.lnkstudy.ui.activity.HomeRightActivity;
import com.bll.lnkstudy.ui.activity.drawing.BookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaperDrawingActivity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;


public class ActivityManager {
    private final Stack<WeakReference<Activity>> stack;

    private ActivityManager() {
        stack = new Stack<>();
    }

    public static ActivityManager getInstance() {
        return HORDEL.instacen;
    }

    private static class HORDEL {
        private static ActivityManager instacen = new ActivityManager();
    }


    public void addActivity(Activity activity) {
        if (activity.getClass().getName().equals(HomeLeftActivity.class.getName())){
            finishActivity(HomeLeftActivity.class.getName());
        }
        if (activity.getClass().getName().equals(HomeRightActivity.class.getName())){
            finishActivity(HomeRightActivity.class.getName());
        }
        stack.push(new WeakReference<>(activity));
    }

    public Activity currentActivity() {
        return stack.peek().get();
    }


    public void finishOthers(Class cls) {

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity a = weak.get();
            if (a == null) {
                it.remove();
                continue;
            }
            if (!a.getClass().getName().equals(cls.getName())) {

                a.finish();
                it.remove();
            }
        }
    }

    public void finishOthers(Activity a) {
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            if (weak.get() == null) {
                it.remove();
                continue;
            }
            if (weak.get() != a) {
                weak.get().finish();
                it.remove();
            }
        }
    }

    public void finishActivity(Activity a) {
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            if (weak.get() == null) {
                it.remove();
                continue;
            }
            if (weak.get() == a) {
                it.remove();
                a.finish();
                return;
            }
        }
    }

    public void finishActivity(String cls) {
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity a=weak.get();
            if (a == null) {
                it.remove();
                continue;
            }
            if (a.getClass().getName().equals(cls)) {
                it.remove();
                a.finish();
                return;
            }
        }
    }


    public void finishAll() {
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            if (weak.get() != null) {
                weak.get().finish();
            }
            it.remove();
        }
    }

    /**
     * 获取当前书籍是否已经打开
     * @param id
     * @return
     */
    public void checkBookIDisExist(int id){
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(BookDetailsActivity.class.getName())) {
                int bookId=activity.getIntent().getIntExtra("book_id",0);
                if (bookId==id){
                    activity.finish();
                    it.remove();
                }
            }
        }
    }

    /**
     * 检查当前作业本是否已经打开
     * @return
     */
    public void checkHomeworkDrawingisExist(HomeworkTypeBean item){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(HomeworkDrawingActivity.class.getName())) {
                HomeworkTypeBean homeworkType= (HomeworkTypeBean) activity.getIntent().getBundleExtra("homeworkBundle").getSerializable("homework");
                if (item.course==homeworkType.course&&item.typeId ==homeworkType.typeId){
                    activity.finish();
                    it.remove();
                }
            }
        }

    }

    /**
     * 检查当前画本是否打开
     * @return
     */
    public void checkPaintingDrawingIsExist(int type){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(PaintingDrawingActivity.class.getName())) {
                int flags= activity.getIntent().getFlags();
                if (flags==type){
                    activity.finish();
                    it.remove();
                }
            }
        }

    }

    /**
     * 检查当前画本是否打开
     * @return
     */
    public void checkPaperDrawingIsExist(int mflags,String mCourse,int mTypeId){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(PaperDrawingActivity.class.getName())) {
                int flags= activity.getIntent().getFlags();
                String course= activity.getIntent().getStringExtra("course");
                int categoryId= activity.getIntent().getIntExtra("categoryId",0);
                if (flags==mflags && course==mCourse && categoryId==mTypeId){
                    activity.finish();
                    it.remove();
                }
            }
        }
    }

}
