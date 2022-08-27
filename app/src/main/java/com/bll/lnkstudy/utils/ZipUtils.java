package com.bll.lnkstudy.utils;

import com.bll.lnkstudy.Constants;
import com.bll.utilssdk.utils.FileUtils;
import com.bll.utilssdk.zip.IZipCallback;
import com.bll.utilssdk.zip.ZipManager;

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
        String destinationStr=Constants.Companion.getZIP_PATH()+File.separator+fileName+".zip";
//        File destinationFile = new File(destinationStr);
//        if(destinationFile.exists()){
//            destinationFile.delete();
//        }
        ZipManager.zip(targetStr,destinationStr,callback);

    }

    /**
     *
     * @param targetZipFilePath  原Zip文件的的绝对文件路径
    * @param fileName  解压出来的文件夹名字
     * @param callback
     */
    public static void unzip(String userId,String targetZipFilePath, String fileName, ZipCallback callback){

        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        File unzipTargetFile = new File(Constants.Companion.getBOOK_PATH(),userId);
        if(!unzipTargetFile.exists()){
            unzipTargetFile.mkdir();
        }
        File targetFile = new File(targetZipFilePath);//验证目标是否存在

        //目标目录
        String fileTargetName = unzipTargetFile.getPath()+File.separator+fileName;


        if(!targetFile.exists()){
            callback.onError("目标Zip不存在");
            return;
        }

        File unZipFile = new File(fileTargetName);

        if(unZipFile.exists()){
            unZipFile.delete();//有了还解压说明原来的不要了
        }else {
            unZipFile.mkdir();//没有，证明需要新建
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
