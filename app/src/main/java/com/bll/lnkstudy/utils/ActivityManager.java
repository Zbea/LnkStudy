package com.bll.lnkstudy.utils;

import android.app.Activity;

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
}
