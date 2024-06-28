package com.bll.lnkstudy.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.bll.lnkstudy.Constants;


public class DeviceUtil {

    private static final String TAG = "DeviceUtil";
    private static final String EINK_CUSTOMER = "ro.sys.project";

    public static String getDeviceCustomer() {
        String customer = SystemProperties.get(EINK_CUSTOMER);
        if (TextUtils.isEmpty(customer)) {
            customer = "sdk103s";
        }
        return customer;
    }

    //产品版本
    public static String getProductVersion() {
        String version = Build.VERSION.INCREMENTAL;
        if (TextUtils.isEmpty(version)) {
            return "";
        }
        return version;
    }

    public static String getOtaProductVersion() {
        String version = SystemProperties.get("ro.product.ota.version");
        if (TextUtils.isEmpty(version)) {
            return "";
        }
        return version;
    }

    //产品序列号
    private static final String SERIAL_NUMBER = "ro.serialno";

    @SuppressLint("MissingPermission")
    private static String getSerialNumber() {
        String version;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            version = Build.getSerial();
        } else {
            version = SystemProperties.get(SERIAL_NUMBER);
        }
        if (TextUtils.isEmpty(version)) {
            return "";
        }
        return version;
    }

    //sn序列号 = sn前缀+产品序列号后9位
    public static String getOtaSerialNumber() {
        String sn = getSerialNumber();
        Log.d("TEST-FU","getOtaSerialNumber,sn = " + sn);
        if(sn.length() != 14 && sn.substring(0,4).equals("QLMB")){
            return getDualOtaSerialNumber();
        }else {
            String snPerfix = SystemProperties.get(Constants.PERSIST_OTA_SN_PREFIX, "");
            if (!TextUtils.isEmpty(snPerfix)) {
                return snPerfix + sn.substring(sn.length() - 9);
            }
            return sn;
        }
    }

    public static String getDualOtaSerialNumber() {
        String sn = getSerialNumber();
        String snPerfix = SystemProperties.get(Constants.PERSIST_OTA_SN_PREFIX, "");
        try{
            if (!TextUtils.isEmpty(snPerfix)) {
                int year = 23 + ((int)sn.charAt(8) - 65);
                int months = (int)(Integer.valueOf(sn.substring(9,11)) * 7)/30;
                String number = sn.substring(sn.length() - 5);
                return snPerfix + year + months + number;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sn;
    }

    public static String getOtaProductName() {
        String productName = SystemProperties.get("ro.product.model");
        if (productName.contains(" ")) {
            productName = productName.replaceAll(" ", "");
        }

        return productName;
    }

    public static String getRemoteHost() {
        String remoteHost = SystemProperties.get("ro.product.ota.host");
        if (remoteHost == null || remoteHost.length() == 0) {
            remoteHost = "hantangfy.top:2300";
        }
        return remoteHost;
    }

    public static String getRemoteHostBackup() {
        String remoteHost = SystemProperties.get("ro.product.ota.host2");
        if (remoteHost == null || remoteHost.length() == 0) {
            remoteHost = "hantangfy.top:2300";
        }
        return remoteHost;
    }

    public static String getCurrentFirmwareVersion() {
        String version = SystemProperties.get("ro.product.version");
        if (TextUtils.isEmpty(version)) {
            return "1.0.0";
        }
        return version;
    }
}
