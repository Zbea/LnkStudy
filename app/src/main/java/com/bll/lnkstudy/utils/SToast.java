package com.bll.lnkstudy.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.StringRes;

import com.bll.lnkstudy.R;
import com.bll.lnkstudy.net.ExceptionHandle;


/**
 * emmmm .........
 * 取名 super Toast 的意思
 * 同一时间只能显示一个toast
 * 支持在任意线程调用
 * 可以取消toast
 * Create by sanvar , 18-11-5
 */
public class SToast {
    private static Context ctx;
    private static Toast toast;
    private static Handler handler;

    public static void initToast(Context context) {
        ctx = context;
        handler = new Handler(ctx.getMainLooper());
    }

    public static void showText(int screen,@StringRes int res) {
        showText(screen,ctx.getString(res));
    }

    public static void showText(int screen, final CharSequence str) {
        if (Thread.currentThread().getId() != 1) {
            // 在子线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    finalShow(screen,str);
                }
            });
        } else {
            finalShow(screen,str);
        }
    }

    private static void finalShow(int screen,CharSequence str) {
        toast = Toast.makeText(ctx, str, Toast.LENGTH_SHORT);
        TextView text = toast.getView().findViewById(android.R.id.message);
        text.setWidth(400);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(Color.WHITE);
        text.setTextSize(20);
        text.setPadding(30, 20, 30, 20);
        toast.getView().setBackground(ctx.getDrawable(R.drawable.bg_black_solid_10dp_corner));
        if (screen==2){
            toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 500, 200);
        }
        else if (screen==3){
            toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 500, 200);
        }
        else {
            toast.setGravity(Gravity.BOTTOM|Gravity.LEFT, 500, 200);
        }
        toast.show();
    }

    /**
     * 取消显示
     * 建议放在 baseActivity中做统一处理
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
