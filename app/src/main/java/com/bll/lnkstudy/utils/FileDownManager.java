package com.bll.lnkstudy.utils;

import android.content.Context;
import android.util.Log;

import com.bll.lnkstudy.Constants;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.List;


public class FileDownManager {

    private static FileDownManager incetance;
    private static Context mContext;
    private String url; //下载的url链接
    private String path;//文件的绝对路径
    private String auth = "";
    private String token = "";


    public static FileDownManager with(Context context) {
//        if (incetance == null) {
//            synchronized (FileDownManager.class) {
//                if (incetance == null) {
//                    incetance = new FileDownManager();
//                }
//            }
//        }
        mContext = context;
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
        auth = "Authorization";
        token = SPUtil.INSTANCE.getString("token");
        Log.d("debug"," download url = "+url);
        Log.d("debug"," path = "+path);
        BaseDownloadTask downloadTask =  FileDownloader.getImpl().create(url)
                .addHeader("Accept-Encoding", "identity")
                .addHeader(auth, token)
                .setForceReDownload(true)
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
