package com.bll.lnkstudy.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bll.lnkstudy.R;
import com.bll.lnkstudy.ui.activity.HomeLeftActivity;


public class ProgressDialog {
    Context context;
    Dialog mDialog;
    int screenPos=0;//当前屏幕位置

    public ProgressDialog(Context context,int screenPos) {
        this.context = context;
        this.screenPos=screenPos;
        createDialog();
    }

    public void createDialog() {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(context, R.layout.dialog_progress, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        Window window = mDialog.getWindow();
        //要加上设置背景，否则dialog宽高设置无作用
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //设置背景不变暗
        layoutParams.dimAmount=0f;
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();  //屏幕宽
        int height = wm.getDefaultDisplay().getHeight();  //屏幕高
        layoutParams.width = 250;
        layoutParams.height = 250;

        //全屏时 加载在a屏
        if (screenPos==3 ){
            layoutParams.gravity= Gravity.CENTER_VERTICAL|Gravity.LEFT;
            layoutParams.x=570;
        }

        //主页、全屏时 加载在b屏
        if (screenPos==3&&context instanceof HomeLeftActivity){
            layoutParams.gravity= Gravity.CENTER_VERTICAL|Gravity.RIGHT;
            layoutParams.x=570;
        }

        window.setAttributes(layoutParams);
    }



    public void show() {
        Activity activity= (Activity) context;
        if (activity!=null && !activity.isFinishing() && !activity.isDestroyed() &&
                mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }



    public void dismiss() {
        Activity activity= (Activity) context;
        if (activity!=null && !activity.isFinishing() && !activity.isDestroyed() &&
                mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}
