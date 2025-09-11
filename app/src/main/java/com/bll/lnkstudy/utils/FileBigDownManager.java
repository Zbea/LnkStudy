package com.bll.lnkstudy.utils;

import android.content.Context;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;


public class FileBigDownManager {

    private static FileBigDownManager incetance;
    private static Context mContext;
    private String url; //下载的url链接
    private String path;//文件的绝对路径
    private String auth = "";
    private String token = "";


    public static FileBigDownManager with(Context context) {
//        if (incetance == null) {
//            synchronized (FileBigDownManager.class) {
//                if (incetance == null) {
//                    incetance = new FileBigDownManager();
//                }
//            }
//        }
        mContext = context;
        return new FileBigDownManager();
    }

    //创建下载链接
    public FileBigDownManager create(String url) {
        this.url = url;
        return this;
    }


    public FileBigDownManager setPath(String path) {
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
                .setPath(path).setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        singletaskCallBack.completed(task);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        singletaskCallBack.error(task, e);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }

                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        singletaskCallBack.progress(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        singletaskCallBack.paused(task, soFarBytes, totalBytes);
                    }
        });
        downloadTask.start();
        return downloadTask;
    }

    public interface SingleTaskCallBack {

        void progress(BaseDownloadTask task, long soFarBytes, long totalBytes);

        void completed(BaseDownloadTask task);

        void paused(BaseDownloadTask task, long soFarBytes, long totalBytes);

        void error(BaseDownloadTask task, Throwable e);
    }

}
