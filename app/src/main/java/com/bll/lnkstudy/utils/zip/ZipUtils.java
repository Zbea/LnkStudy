package com.bll.lnkstudy.utils.zip;

import com.bll.lnkstudy.FileAddress;
import com.bll.lnkstudy.utils.zip.IZipCallback;
import com.bll.lnkstudy.utils.zip.ZipManager;

import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;


public class ZipUtils {

    /**
     * 压缩
     * @param targetStr 目标文件路径
     * @param fileName 压缩文件名称
     * @param callback
     */
    public static void zip(String targetStr, String fileName, IZipCallback callback){

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
    * @param fileTargetPath  解压出来地址
     * @param callback
     */
    public static void unzip(String targetZipFilePath, String fileTargetPath, IZipCallback callback){

        if (!Zip4jUtil.isStringNotNullAndNotEmpty(targetZipFilePath) || !Zip4jUtil.isStringNotNullAndNotEmpty(fileTargetPath)) {
            if (callback != null) callback.onError("路径不能为空");
            return;
        }

        if(!new File(targetZipFilePath).exists()){
            if (callback != null) callback.onError("目标Zip不存在");
            return;
        }

        File unZipFile = new File(fileTargetPath);
        if(unZipFile.exists()){
            unZipFile.delete();
        }else {
            unZipFile.mkdir();
        }
        //开始解压
        ZipManager.unzip(targetZipFilePath,fileTargetPath,callback);
    }


}
