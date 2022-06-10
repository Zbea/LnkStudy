package com.bll.lnkstudy.utils;

import android.content.Context;

public class DP2PX {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 35.     * 将px值转换为sp值，保证文字大小不变
     * 36.     *
     * 37.     * @param pxValue
     * 38.     * @param fontScale
     * 39.     *            （DisplayMetrics类中属性scaledDensity）
     * 40.     * @return
     * 41.
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 48.     * 将sp值转换为px值，保证文字大小不变
     * 49.     *
     * 50.     * @param spValue
     * 51.     * @param fontScale
     * 52.     *            （DisplayMetrics类中属性scaledDensity）
     * 53.     * @return
     * 54.
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
