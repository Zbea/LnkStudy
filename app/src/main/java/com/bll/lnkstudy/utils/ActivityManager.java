package com.bll.lnkstudy.utils;

import android.app.Activity;

import com.bll.lnkstudy.mvp.model.HomeworkType;
import com.bll.lnkstudy.mvp.model.Note;
import com.bll.lnkstudy.ui.activity.BookDetailsActivity;
import com.bll.lnkstudy.ui.activity.HomeworkDrawingActivity;
import com.bll.lnkstudy.ui.activity.NoteDrawingActivity;
import com.bll.lnkstudy.ui.activity.PaintingDrawingActivity;
import com.bll.lnkstudy.ui.activity.PaperDrawingActivity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;


public class ActivityManager {
    private Stack<WeakReference<Activity>> stack;

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
    public boolean checkBookIDisExist(long id){
        boolean isExist=false;
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(BookDetailsActivity.class.getName())) {
                long bookId=activity.getIntent().getLongExtra("book_id",0);
                if (bookId==id){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    /**
     * 检查当前作业本是否已经打开
     * @return
     */
    public boolean checkHomeworkDrawingisExist(HomeworkType item){
        boolean isExist=false;
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(HomeworkDrawingActivity.class.getName())) {
                HomeworkType homeworkType= (HomeworkType) activity.getIntent().getBundleExtra("homeworkBundle").getSerializable("homework");
                if (item.courseId==homeworkType.courseId&&item.type==homeworkType.type){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    /**
     * 检查当前画本是否打开
     * @return
     */
    public boolean checkPaintingDrawingIsExist(int type){
        boolean isExist=false;
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(PaintingDrawingActivity.class.getName())) {
                int flags= activity.getIntent().getFlags();
                if (flags==type){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    /**
     * 检查当前画本是否打开
     * @return
     */
    public boolean checkPaperDrawingIsExist(int mflags,int mCourseId,int mTypeId){
        boolean isExist=false;
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(PaperDrawingActivity.class.getName())) {
                int flags= activity.getIntent().getFlags();
                int courseId= activity.getIntent().getIntExtra("courseId",0);
                int categoryId= activity.getIntent().getIntExtra("categoryId",0);
                if (flags==mflags && courseId==mCourseId && categoryId==mTypeId){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    /**
     * 检查笔记是否打开
     * @return
     */
    public boolean checkNoteDrawing(Note note){
        boolean isExist=false;
        Iterator<WeakReference<Activity>> it = stack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> weak = it.next();
            Activity activity=weak.get();
            if (activity.getClass().getName().equals(NoteDrawingActivity.class.getName())) {
                Note mNote= (Note) activity.getIntent().getBundleExtra("noteBundle").getSerializable("note");
                if (note.id==mNote.id){
                    isExist=true;
                }
            }
        }
        return isExist;
    }

}
