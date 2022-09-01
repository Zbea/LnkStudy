package com.bll.lnkstudy.utils;

import android.content.Context;

import java.util.List;
import java.util.Map;

public class ToolUtils {


    //返回图片唯一值(用于存储)
    public static String getImageResStr(Context context,int resId){
        if (resId==0){
            return "";
        }
        return context.getResources().getResourceEntryName(resId);
    }

    //图片唯一值转为资源id
    public static int getImageResId(Context context,String resStr){
        if (resStr.isEmpty())
        {
            return 0;
        }
        return context.getResources().getIdentifier(resStr,"mipmap", context.getPackageName());
    }

}
