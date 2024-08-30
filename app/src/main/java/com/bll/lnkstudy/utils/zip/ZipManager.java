package com.bll.lnkstudy.utils.zip;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * function:ZIP 压缩工具管理器
 */
public final class ZipManager {
    private ZipManager() {}

    /**
     * 是否打印日志
     */
    public static void debug(boolean debug) {
        ZipLog.config(debug);
    }

    private static final int     WHAT_START    = 100;
    private static final int     WHAT_FINISH   = 101;
    private static final int     WHAT_PROGRESS = 102;
    private static       Handler mUIHandler    = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_START:
                    ((IZipCallback) msg.obj).onStart();
                    ZipLog.debug("onStart.");
                    break;
                case WHAT_PROGRESS:
                    ((IZipCallback) msg.obj).onProgress(msg.arg1);
                    ZipLog.debug("onProgress: percentDone=" + msg.arg1);
                    break;
                case WHAT_FINISH:
                    ((IZipCallback) msg.obj).onFinish();
                    ZipLog.debug("onFinish: success=true");
                    break;
            }
        }
    };

    /**
     * 压缩文件或者文件夹
     *
     * @param targetPath          被压缩的文件路径
     * @param destinationFilePath 压缩后生成的文件路径
     * @param callback            压缩进度回调
     */
    public static void zip(String targetPath, String destinationFilePath, IZipCallback callback) {
        zip(targetPath, destinationFilePath, "", callback);
    }

    /**
     * 压缩文件或者文件夹
     *
     * @param targetPath          被压缩的文件路径
     * @param destinationFilePath 压缩后生成的文件路径
     * @param password            压缩加密 密码
     * @param callback            压缩进度回调
     */
    public static void zip(String targetPath, String destinationFilePath, String password, IZipCallback callback) {
        ZipLog.debug("zip: targetPath=" + targetPath + " , destinationFilePath=" + destinationFilePath + " , password=" + password);
        try {
            ZipParameters parameters = new ZipParameters();
            ZipFile zipFile = new ZipFile(destinationFilePath);
            if (password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                zipFile.setPassword(password.toCharArray());
            }
            File targetFile = new File(targetPath);
            File[] files=targetFile.listFiles();
            for (File file :files){
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
            timerMsg(callback, zipFile,"zip");
        } catch (Exception e) {
            if (callback != null) callback.onError("压缩失败");
            ZipLog.debug("zip: Exception=" + e.getMessage());
        }
    }

    /**
     * 压缩文件或者文件夹
     *
     * @param targetPaths          被压缩的文件路径集合
     * @param destinationFilePath 压缩后生成的文件路径
     * @param password            压缩加密 密码
     * @param callback            压缩进度回调
     */
    public static void zip(List<String> targetPaths, String destinationFilePath, String password, IZipCallback callback) {
        ZipLog.debug("zip: targetPath=" + targetPaths.toString() + " , destinationFilePath=" + destinationFilePath + " , password=" + password);
        try {
            ZipParameters parameters = new ZipParameters();
            ZipFile zipFile = new ZipFile(destinationFilePath);
            if (password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                zipFile.setPassword(password.toCharArray());
            }
            for (String path: targetPaths) {
                File file=new File(path);
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
            timerMsg(callback, zipFile,"zip");
        } catch (Exception e) {
            if (callback != null) callback.onError("压缩失败");
            ZipLog.debug("zip: Exception=" + e.getMessage());
        }
    }

    /**
     * 解压
     *
     * @param targetZipFilePath     待解压目标文件地址
     * @param destinationFolderPath 解压后文件夹地址
     * @param callback              回调
     */
    public static void unzip(String targetZipFilePath, String destinationFolderPath, IZipCallback callback) {
        unzip(targetZipFilePath, destinationFolderPath, "", callback);
    }

    /**
     * 解压
     *
     * @param targetZipFilePath     待解压目标文件地址
     * @param destinationFolderPath 解压后文件夹地址
     * @param password              解压密码
     * @param callback              回调
     */
    public static void unzip(String targetZipFilePath, String destinationFolderPath, String password, final IZipCallback callback) {
        ZipLog.debug("unzip: targetZipFilePath=" + targetZipFilePath + " , destinationFolderPath=" + destinationFolderPath + " , password=" + password);
        try {
            ZipFile zipFile = new ZipFile(targetZipFilePath);
            if (!zipFile.isValidZipFile()){
                if (callback != null) callback.onError("文件格式不正确");
            }
            if (zipFile.isEncrypted() && Zip4jUtil.isStringNotNullAndNotEmpty(password)) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.setRunInThread(true);
            zipFile.extractAll(destinationFolderPath);
            timerMsg(callback, zipFile,"unzip");
        } catch (Exception e) {
            if (callback != null) callback.onError("解压失败");
            ZipLog.debug("unzip: Exception=" + e.getMessage());
        }
    }

    //Handler send msg
    private static void timerMsg(final IZipCallback callback, ZipFile zipFile, String index) {
        if (callback == null) return;
        mUIHandler.obtainMessage(WHAT_START, callback).sendToTarget();
        final ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
        final Timer           timer           = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mUIHandler.obtainMessage(WHAT_PROGRESS, progressMonitor.getPercentDone(), 0, callback).sendToTarget();
                if (progressMonitor.getResult() == ProgressMonitor.Result.SUCCESS) {
                    mUIHandler.obtainMessage(WHAT_FINISH, callback).sendToTarget();
                    this.cancel();
                    timer.purge();
                }
            }
        }, 0, 300);
    }

}
