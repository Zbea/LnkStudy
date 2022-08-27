package com.bll.lnkstudy.manager;

import android.content.Context;
import android.util.Log;

import com.bll.lnkstudy.Constants;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.StringUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by ly on 2021/1/20 9:45
 */
public class FileDownManager {

    private static FileDownManager incetance;
    private static Context mContext;
    private String url; //下载的url链接
    private String path;//文件的绝对路径
    private String auth = "";
    private String token = "";


    public static FileDownManager with(Context context) {
        if (incetance == null) {
            synchronized (FileDownManager.class) {
                if (incetance == null) {
                    incetance = new FileDownManager();
                }
            }
        }
        mContext = context;
        return incetance;
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


    public FileDownManager addHeader(String auth, String token) {
        this.auth = auth;
        this.token = token;
        return this;
    }

    //单任务下载
    public BaseDownloadTask startDownLoad(final DownLoadCallBack downLoadCallBack) {
        auth = Constants.Companion.getAUTH();
        token = SPUtil.INSTANCE.getString("token");
        Log.d("debug"," download url = "+url);
        BaseDownloadTask downloadTask =  FileDownloader.getImpl().create(url).addHeader(auth, token).setPath(path).setListener(new FileDownloadListener() {

            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void connected(final BaseDownloadTask task, final String etag, final boolean isContinue, final int soFarBytes, final int totalBytes) {
//                downLoadCallBack.connected(task,etag,isContinue,soFarBytes,totalBytes);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                downLoadCallBack.progress(task, soFarBytes, totalBytes);

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                downLoadCallBack.completed(task);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                downLoadCallBack.paused(task, soFarBytes, totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                downLoadCallBack.error(task, e);
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        });
        downloadTask.start();
        return downloadTask;
    }

    //final FileDownloadListener queueTarget = new FileDownloadListener() {
    //    @Override
    //    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    //    }
    //
    //    @Override
    //    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
    //    }
    //
    //    @Override
    //    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    //    }
    //
    //    @Override
    //    protected void blockComplete(BaseDownloadTask task) {
    //    }
    //
    //    @Override
    //    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
    //    }
    //
    //    @Override
    //    protected void completed(BaseDownloadTask task) {
    //    }
    //
    //    @Override
    //    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    //    }
    //
    //    @Override
    //    protected void error(BaseDownloadTask task, Throwable e) {
    //    }
    //
    //    @Override
    //    protected void warn(BaseDownloadTask task) {
    //    }
    //};
    //
    //// 第一种方式 :
    //
    ////for (String url : URLS) {
    ////    FileDownloader.getImpl().create(url)
    ////            .setCallbackProgressTimes(0) // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
    ////            .setListener(queueTarget)
    ////            .asInQueueTask()
    ////            .enqueue();
    ////}
    //
    ////if(serial){
    //    // 串行执行该队列
    ////    FileDownloader.getImpl().start(queueTarget, true);
    //// }
    //
    //// if(parallel){
    //    // 并行执行该队列
    ////    FileDownloader.getImpl().start(queueTarget, false);
    ////}
    //
    //// 第二种方式:
    //
    //final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
    //
    //final List<BaseDownloadTask> tasks = new ArrayList<>();
    //for (int i = 0; i < count; i++) {
    //     tasks.add(FileDownloader.getImpl().create(Constant.URLS[i]).setTag(i + 1));
    //}
    //
    //queueSet.disableCallbackProgressTimes(); // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
    //
    //// 所有任务在下载失败的时候都自动重试一次
    //queueSet.setAutoRetryTimes(1);
    //
    //if (serial) {
    //  // 串行执行该任务队列
    //     queueSet.downloadSequentially(tasks);
    //     // 如果你的任务不是一个List，可以考虑使用下面的方式，可读性更强
    ////      queueSet.downloadSequentially(
    ////              FileDownloader.getImpl().create(url).setPath(...),
    ////              FileDownloader.getImpl().create(url).addHeader(...,...),
    ////              FileDownloader.getImpl().create(url).setPath(...)
    ////      );
    //}
    //
    //if (parallel) {
    //  // 并行执行该任务队列
    //   queueSet.downloadTogether(tasks);
    //   // 如果你的任务不是一个List，可以考虑使用下面的方式，可读性更强
    ////    queueSet.downloadTogether(
    ////            FileDownloader.getImpl().create(url).setPath(...),
    ////            FileDownloader.getImpl().create(url).setPath(...),
    ////            FileDownloader.getImpl().create(url).setSyncCallback(true)
    ////    );
    //}
    //
    //// 串行任务动态管理也可以使用FileDownloadSerialQueue。


    public interface DownLoadCallBack {

//        void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes);

        void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void completed(BaseDownloadTask task);

        void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);

        void error(BaseDownloadTask task, Throwable e);
    }

}
