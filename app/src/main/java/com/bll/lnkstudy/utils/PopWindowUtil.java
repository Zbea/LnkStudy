package com.bll.lnkstudy.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopWindowUtil {
//    private static PopWindowUtil instance;
    private PopupWindow mPopupWindow;
    private View contentView;
    private View view;
    private int ox;
    private int oy;
    private int gravity;

//    // 私有化构造方法，变成单例模式
//    private PopWindowUtil() {
//
//    }
//
//    // 对外提供一个该类的实例，考虑多线程问题，进行同步操作
//    public static PopWindowUtil getInstance() {
//        if (instance == null) {
//            synchronized (PopWindowUtil.class) {
//                if (instance == null) {
//                    instance = new PopWindowUtil();
//                }
//            }
//        }
//        return instance;
//    }


    /**
     *
     * @param cx
     * @param view 浮漂的view  标记view
     * @param view1   pop弹出的view
     * @param ox  偏移的x
     * @param oy  偏移的y
     * @param gravity gravity left top end bottom
     * @return
     */
    public PopWindowUtil makePopupWindow(Context cx, View view, View view1, int ox,int oy,int gravity) {

        mPopupWindow = new PopupWindow(cx);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.view=view;
        contentView=view1;
        this.ox=ox;
        this.oy=oy;
        this.gravity=gravity;

        measureView(contentView);
        // 设置PopupWindow的内容view
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
        mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        return this;
    }

    /**
     * 测量view
     * @param view
     */
    public void measureView(View view){
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
    }

    public void dismiss(){
        if(mPopupWindow !=null){
            mPopupWindow.dismiss();
        }
    }

    public void show(){
        if(mPopupWindow !=null){
            mPopupWindow.showAsDropDown(view,ox, oy,gravity);
        }
    }


}
