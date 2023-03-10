package com.bll.lnkstudy.utils;

import android.content.Context;
import android.widget.RadioButton;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtils {

    public static String getImagesStr(List<String> images){
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
        String regex = "[1][3-9]\\d{9}";
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

}
