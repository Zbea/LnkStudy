package com.bll.lnkstudy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;

import com.bll.lnkstudy.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtils {
    public static String[] numbers = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九","十","十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九","二十"
            ,"二一", "二二", "二三", "二四", "二五", "二六", "二七", "二八", "二九","三十"
            ,"三一", "三二", "三三", "三四", "三五", "三六", "三七", "三八", "三九","四十"
            ,"四一", "四二", "四三", "四四", "四五", "四六", "四七", "四八", "四九","五十"
            ,"五一", "五二", "五三", "五四", "五五", "五六", "五七", "五八", "五九","六十"
    }; // 大写数字


    /**
     * list集合平分两份
     * @param originalList
     * @return
     * @param <T>
     */
    public static <T> List<T>[] splitList(List<T> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return new List[]{new ArrayList<>(), new ArrayList<>()};
        }

        // 计算中间索引（长度为奇数时，前半部分多一个）
        int mid = (originalList.size() + 1) / 2;

        // 前半部分：从 0 到 mid-1（包含）
        List<T> firstPart = new ArrayList<>(originalList.subList(0, mid));
        // 后半部分：从 mid 到末尾
        List<T> secondPart = new ArrayList<>(originalList.subList(mid, originalList.size()));

        return new List[]{firstPart, secondPart};
    }

    /**
     * 格式化数据显示
     * @param num
     * @param format
     * @return
     */
    public static String getFormatNum(Object num, String format){
        return new DecimalFormat(format).format(num);
    }

    /**
     * 得到唯一id
     * @return
     */
    public static int getDateId(){
        long currentTimeMillis = System.currentTimeMillis();
        return (int) (currentTimeMillis % Integer.MAX_VALUE);
    }

    public static String getImagesStr(List<?> images){
        String url="";
        for (int i = 0; i < images.size(); i++) {
            if (i== images.size()-1){
                url+=images.get(i);
            }else {
                url+=images.get(i)+",";
            }
        }
        return url;
    }

    //返回图片唯一值(用于存储)
    public static String getImageResStr(Context context,int resId){
        if (resId==0){
            return "";
        }
        return context.getResources().getResourceEntryName(resId);
    }

    //图片唯一值转为资源id
    public static int getImageResId(Context context,String resStr){
        if (resStr==null)
        {
            return 0;
        }
        return context.getResources().getIdentifier(resStr,"mipmap", context.getPackageName());
    }

    /**
     * 判断是否是11位的手机号
     * @param str
     * @return
     */
    public static boolean isPhoneNum(String str){
        String regex = "1[3-9]\\d{9}";
        return str.matches(regex);
    }

    /**
     * 判断验证码 长度 4位 6位
     * @param str
     * @return
     */
    public static  boolean isVerifyCode(String str){
        String regex4 = "\\d{4}";
        String regex6 = "\\d{6}";
        return str.matches(regex4)||str.matches(regex6);
    }

    //是否是email
    public static boolean isEmailAddress(String phone) {
        String regex = "^^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 是否包含大小写字母及数字或
     * @param str
     * @param min  最低多少
     * @param max  最大多少
     * @return
     */
    public static boolean isLetterOrDigit(String str,int min,int max) {
        String regex = "^[a-zA-Z0-9]{"+min+","+max+"}$";
        return str.matches(regex);
    }

    /**
     * json解析是去掉回车空格换行 其他
     *    注：\n 回车(\u000a)
     *     \t 水平制表符(\u0009)
     *     \s 空格(\u0008)
     *     \r 换行(\u000d)
     *
     * @param str
     * @return 完整的string
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
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

}
