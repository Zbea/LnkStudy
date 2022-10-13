package com.bll.lnkstudy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bll.lnkstudy.Constants;
import com.bll.lnkstudy.R;
import com.bll.lnkstudy.utils.DP2PX;


/**
 * Created by dell on 2017/10/23.
 */

public class CommonDialog {
    private Context context;
    private AlertDialog dialog;
    private TextView okTv;
    private String title="";
    private boolean is;
    private String content="";//提示文案
    private String cancle="取消";//取消文案
    private String ok ="确认";//确认文案
    private int screenPos=0;

    public CommonDialog(Context context) {
        this.context = context;
    }
    public CommonDialog(Context context,int screenPos) {
        this.context = context;
        this.screenPos=screenPos;
    }

    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    public CommonDialog setContent(String content) {
        this.content = content;
        return this;
    }
    public CommonDialog setCancel(String cancle) {
        this.cancle = cancle;
        return this;
    }
    public CommonDialog setOk(String ok) {
        this.ok = ok;
        return this;
    }

    public CommonDialog builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_com, null);
        TextView titleTv = view.findViewById(R.id.tv_dialog_title);
        TextView contentTv = view.findViewById(R.id.tv_dialog_content);
        TextView cancleTv = view.findViewById(R.id.tv_cancle);
        okTv=view.findViewById(R.id.tv_ok);

        if(!TextUtils.isEmpty(title)) titleTv.setText(title);
        if (!is)
        {
            titleTv.setVisibility(View.GONE);
            contentTv.setMinHeight(220);
        }
        if(!TextUtils.isEmpty(content)) contentTv.setText(content);
        if(!TextUtils.isEmpty(cancle)) cancleTv.setText(cancle);
        if(!TextUtils.isEmpty(ok))okTv.setText(ok);

        cancleTv.setOnClickListener(v -> {
            cancel();
            if(onDialogClickListener !=null) onDialogClickListener.cancel();
        });
        okTv.setOnClickListener(v -> {
            cancel();
            if(onDialogClickListener !=null) onDialogClickListener.ok();
        });
        dialog = new AlertDialog.Builder(new ContextThemeWrapper(context,R.style.styleDialogCustom)).create();
        dialog.setView(view);
        dialog.show();
        Window window=dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = 600;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (screenPos==3){
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            lp.x=(Constants.Companion.getWIDTH()- 600)/2;
        }

        window.setAttributes(lp);
        return this;
    }

    public CommonDialog setTitleView(boolean is) {
        this.is=is;
        return this;
    }

    public void show() {
        dialog.show();
    }
    public void cancel() {
        dialog.dismiss();
    }
    public OnDialogClickListener onDialogClickListener;
    public interface OnDialogClickListener {
        void cancel();
        void ok();
    }
    public void setDialogClickListener(OnDialogClickListener onDialogClickListener){
        this.onDialogClickListener = onDialogClickListener;
    }
}
