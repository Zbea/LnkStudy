package com.bll.lnkstudy.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.bll.lnkstudy.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDownLoadUtils {

    /**
     * 图片下载
     */
    private String[] urls;//图片地址
    private Context context;
    private String path;//路径
    private File file = null;

    private Map<Integer,String> map=new HashMap<>();//下载成功
    private List<Integer> unLoadList=new ArrayList<>();//未下载成功



    public ImageDownLoadUtils(Context context, String[] urls,String path){
        this.context=context;
        this.urls=urls;
        this.path=path;
    }

    public void startDownload() {
        if (urls==null||urls.length==0)
            return;
        for (int i = 0; i < urls.length; i++) {
            download(i,urls[i]);
        }
    }

    private void download(int index,String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                            .get();
                    if (bitmap != null){
                        saveBmpGallery(bitmap,index+1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bitmap != null && file!=null) {
                        map.put(index,file.getPath());
                    } else {
                        unLoadList.add(index);
                    }
                    if (index==urls.length-1){
//                        Log.i("debug","结束回调");
                        if (map.size()==urls.length)
                        {
                            callBack.onDownLoadSuccess(map);
                        }
                        if (unLoadList.size()>0){
                            callBack.onDownLoadFailed(unLoadList);
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 保存bitmap刷新相册
     *
     * @param bmp     获取的bitmap数据
     */
    private void saveBmpGallery(Bitmap bmp,int i) {
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            file = new File(path,i+".png");
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(file);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "", "");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

    }

    private ImageDownLoadCallBack callBack;

    public void setCallBack(ImageDownLoadCallBack callBack){
        this.callBack=callBack;
    }


    public interface ImageDownLoadCallBack{
        void onDownLoadSuccess(Map<Integer,String> map);
        void onDownLoadFailed(List<Integer> unLoadList);
    }


}
