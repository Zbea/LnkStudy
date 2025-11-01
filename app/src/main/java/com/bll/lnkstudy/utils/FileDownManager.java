package com.bll.lnkstudy.utils;

import android.util.Log;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;


public class FileDownManager {

    private String url; //下载的url链接
    private String path;//文件的绝对路径

    public static FileDownManager with() {
        return  new FileDownManager();
    }

    //创建下载链接
    public FileDownManager create(String url) {
        this.url = url;
        return this;
    }

    public FileDownManager setPath(String path) {
        this.path = path;
        return this;
    }

    //单任务下载
    public BaseDownloadTask startSingleTaskDownLoad(final SingleTaskCallBack singletaskCallBack) {
        Log.d("debug"," download url = "+url);
        Log.d("debug"," path = "+path);
        BaseDownloadTask downloadTask =  FileDownloader.getImpl().create(url)
                .addHeader("Accept-Encoding", "identity")
                .addHeader("Authorization", SPUtil.INSTANCE.getString("token"))
                .setForceReDownload(true)
                .setAutoRetryTimes(2)
                .setPath(path).setListener(new FileDownloadListener() {

            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void connected(final BaseDownloadTask task, final String etag, final boolean isContinue, final int soFarBytes, final int totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                singletaskCallBack.progress(task, soFarBytes, totalBytes);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                singletaskCallBack.completed(task);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                singletaskCallBack.paused(task, soFarBytes, totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                singletaskCallBack.error(task, e);
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        });
        downloadTask.start();
        return downloadTask;
    }

    public interface SingleTaskCallBack {

        void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void completed(BaseDownloadTask task);

        void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void error(BaseDownloadTask task, Throwable e);
    }

}
