package com.bll.utilssdk.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by ly on 2020/3/26 13:59
 */
public class AppUtils {
    public final static String WIDTH = "width";

    public final static String HEIGHT = "height";

    /**
     * px转dp
     *
     * @param context The context
     * @param px      the pixel value
     * @return value in dp
     */
    public static int pxToDp(Context context, float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((px / displayMetrics.density) + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取应用版本名
     *
     * @return 成功返回版本名， 失败返回null
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }

        return null;
    }

    /**
     * @param context 上下文信息
     * @return 获取包信息
     * getPackageName()是当前类的包名，0代表获取版本信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void installApp(Context context, String filePath) {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    context
                    , "com.bll.utilssdk"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    public static void getSystemProperty() {
        Field[] fields = Build.class.getFields();
        for (Field f : fields) {
            try {
                String name = f.getName();
                Object value = f.get(name);

                System.out.println("key:" + name + ":value:" + value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
//        try {
//            Class build = Class.forName("android.os.Build");
//            String customName = (String) build.getDeclaredField("MODEL").toString();
//            return customName;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }

    /**
     * 清除缓存数据
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void clearAppData(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            manager.clearApplicationUserData();
        }

    }

    /**
     * 判断当前应用是否是debug状态
     */

    public static boolean isApkInDebug(Context context) {
        if (context == null){
            return false;
        }
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否是终端PDA
     * @return
     */
//    public static boolean HCPDA(){
//        if ("HC".equals(getSystemProperty())){
//            return true;
//        }
//        return false;
//    }

    /**
     * 关闭当前进程
     */
    public static void closeAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取手机名称
     *
     * @return 手机型号
     */
    public static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getModelName() {
        return Build.MODEL;
    }

    /**
     * 获取手机名称
     *
     * @return 手机名称
     */
    public static String getMobileName() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取产品名称
     *
     * @return 产品名称
     */
    public static String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * 未加密
     *
     * @return 设备ID
     */
    public static String getAndroidID(Context context) {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        return myDevice.getName();
    }

    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    public static final String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 根据apk路径获取包名
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getApkInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        String name = "";
        if (info != null) {
            appInfo = info.applicationInfo;
            name = appInfo.packageName;//此为apk包名    }}
        }
        return name;
    }

    //根据包名启动app
    public static void startAPP(Context context, String appPackageName) throws Exception {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        context.startActivity(intent);
    }

    //重启app
    public static void reOpenApk(Context context){
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        //杀掉以前进程
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
