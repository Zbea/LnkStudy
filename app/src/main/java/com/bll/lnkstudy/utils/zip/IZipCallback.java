package com.bll.lnkstudy.utils.zip;

/**
 * function:压缩回调
 */
public interface IZipCallback {
    /**
     * 开始
     */
    void onStart();

    /**
     * 进度回调
     *
     * @param percentDone 完成百分比
     */
    void onProgress(int percentDone);

    /**
     * 完成
     */
    void onFinish();

    /**
     * 错误
     *
     * @param msg
     */
    void onError(String msg);
}
