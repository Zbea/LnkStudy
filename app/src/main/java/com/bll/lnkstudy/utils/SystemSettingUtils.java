package com.bll.lnkstudy.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.bll.lnkstudy.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SystemSettingUtils {

    /**
     * 跳转系统wifi设置
     *
     * @param context
     */
    public static void gotoSystemWifi(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转系统网络设置
     *
     * @param context
     */
    public static void gotoSystemNet(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转系统设置
     *
     * @param context
     */
    public static void gotoSystemSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 设置飞行模式
     *
     * @param context
     * @param enable
     */
    public static void setAirPlaneMode(Context context, boolean enable) {
//        Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
//        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        intent.putExtra("state", enable);
//        context.sendBroadcast(intent);

        Intent intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    //判断当前飞行模式状态
    public static boolean isAirPlanMode(Context context) {
        int mode = Settings.Global.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return mode == 1;
    }

    /**
     * 全屏截图
     *
     * @param context
     */
    public static void saveScreenShot(Activity context) {
        //截图
        View view = context.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);//允许绘制缓存
        Bitmap bitmap = view.getDrawingCache();//绘制
        BitmapUtils.saveBmpGallery(context,bitmap,Constants.Companion.getIMAGE_PATH(),"screenShot" + System.currentTimeMillis());
    }

    /**
     * 截图特定view
     *
     * @param context
     * @param view
     */
    public static void saveScreenShot(Activity context, View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        BitmapUtils.saveBmpGallery(context,bitmap,Constants.Companion.getIMAGE_PATH(),"screenShot" + System.currentTimeMillis());
    }

    /**
     * 截图
     * @param context
     * @param view
     * @param picName
     */
    public static void saveScreenShot(Activity context, View view,String picName) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        BitmapUtils.saveBmpGallery(context,bitmap,Constants.Companion.getIMAGE_PATH(),picName);
    }

    //获取最大多媒体音量
    public static int getMediaMaxVolume(Context context){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        return mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
    }
    //获取当前多媒体音量
    public static int getMediaVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 设置多媒体音量
    public static void setMediaVolume(Context context, int volume) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, //音量类型
                volume,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

}
