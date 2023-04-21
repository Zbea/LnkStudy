package com.bll.lnkstudy.utils;

import com.bll.lnkstudy.FileAddress;
import com.bll.lnkstudy.utils.zip.IZipCallback;
import com.bll.lnkstudy.utils.zip.ZipManager;

import java.io.File;

/**
 * Created by ly on 2021/1/20 16:20
 */
public class ZipUtils {

    /**
     * 压缩
     * @param targetStr 目标文件路径
     * @param fileName 压缩文件名称
     * @param callback
     */
    public static void zip(String targetStr, String fileName, ZipCallback callback){

        if(!new File(targetStr).exists()){
            callback.onError("目标文件不存在");
            return;
        }
        String destinationStr=new FileAddress().getPathZip(fileName);
        ZipManager.zip(targetStr,destinationStr,callback);

    }

    /**
     *
     * @param targetZipFilePath  原Zip文件的的绝对文件路径
    * @param fileName  解压出来的文件夹名字
     * @param callback
     */
    public static void unzip(String targetZipFilePath, String fileName, ZipCallback callback){

        File targetFile = new File(targetZipFilePath);//验证目标是否存在
        if(!targetFile.exists()){
            callback.onError("目标Zip不存在");
            return;
        }

        String fileTargetName=new FileAddress().getPathTextBook(fileName);

        File unZipFile = new File(fileTargetName);

        if(unZipFile.exists()){
            unZipFile.delete();
        }else {
            unZipFile.mkdir();
        }

        //开始解压
        ZipManager.unzip(targetZipFilePath,fileTargetName,callback);
    }

    public interface ZipCallback extends IZipCallback {

        /**
         * 错误
         *
         * @param msg
         */
        void onError(String msg);
    }
}
