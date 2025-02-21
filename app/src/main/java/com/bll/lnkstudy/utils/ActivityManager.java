package com.bll.lnkstudy.utils;

import android.app.Activity;

import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.book.TextbookBean;
import com.bll.lnkstudy.ui.activity.book.TextBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.CalligraphyDrawingActivity;
import com.bll.lnkstudy.ui.activity.book.HomeworkBookDetailsActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.drawing.TestPaperDrawingActivity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Objects;
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
     * 获取当前课本是否已经打开
     * @return
     */
    public void checkTextBookIsExist(TextbookBean book){
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(TextBookDetailsActivity.class.getName())) {
                int bookId=activity.getIntent().getIntExtra("bookId",0);
                if (bookId==book.bookId){
                    activity.finish();
                    it.remove();
                }
            }
        }
    }

    /**
     * 获取当前作业书是否已经打开
     * @return
     */
    public void checkHomeworkBookIsExist(HomeworkTypeBean typeBean){
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(HomeworkBookDetailsActivity.class.getName())) {
                HomeworkTypeBean beam= (HomeworkTypeBean) activity.getIntent().getBundleExtra("homeworkBundle").getSerializable("homework");
                if (beam.typeId==typeBean.typeId){
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
    public void checkHomeworkDrawingIsExist(HomeworkTypeBean item){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(HomeworkDrawingActivity.class.getName())) {
                HomeworkTypeBean homeworkType= (HomeworkTypeBean) activity.getIntent().getBundleExtra("homeworkBundle").getSerializable("homework");
                if (Objects.equals(item.course, homeworkType.course) &&item.typeId ==homeworkType.typeId){
                    activity.finish();
                    it.remove();
                }
            }
        }

    }

    /**
     * 检查当前作业卷是否打开
     * @return
     */
    public void checkHomeworkPaperDrawingIsExist(HomeworkTypeBean typeBean){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(HomeworkPaperDrawingActivity.class.getName())) {
                HomeworkTypeBean bean= (HomeworkTypeBean) activity.getIntent().getBundleExtra("homeworkBundle").getSerializable("homework");
                if (Objects.equals(bean.course, typeBean.course) && bean.typeId==typeBean.typeId){
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
    public void checkPaintingDrawingIsExist(){
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(PaintingDrawingActivity.class.getName())) {
                activity.finish();
                it.remove();
            }
        }
    }

    /**
     * 检查当前书法是否打开
     * @return
     */
    public void checkCalligraphyDrawingIsExist(){
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(CalligraphyDrawingActivity.class.getName())) {
                activity.finish();
                it.remove();
            }
        }
    }

    /**
     * 检查当前画本是否打开
     * @return
     */
    public void checkPaperDrawingIsExist(String mCourse,int mTypeId){

        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(TestPaperDrawingActivity.class.getName())) {
                String course= activity.getIntent().getStringExtra("course");
                int categoryId= activity.getIntent().getIntExtra("typeId",0);
                if (Objects.equals(course, mCourse) && categoryId==mTypeId){
                    activity.finish();
                    it.remove();
                }
            }
        }
    }



}
